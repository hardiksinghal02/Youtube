package com.youtube.be.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.be.dao.ContentDao;
import com.youtube.be.dao.ContentResourceDao;
import com.youtube.be.dto.TranscodingStatusDto;
import com.youtube.be.entity.ContentEntity;
import com.youtube.be.entity.ContentResourceEntity;
import com.youtube.be.enums.ContentState;
import com.youtube.be.enums.ResourceFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TranscodingUpdateConsumer {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    ContentResourceDao contentResourceDao;

    @KafkaListener(
            topics = "transcoding-update-topic",
            groupId = "transcoding-status-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, Object> message) {
        ObjectMapper objectMapper = new ObjectMapper();
        TranscodingStatusDto transcodingStatusDto =
                objectMapper.convertValue(message.value(), TranscodingStatusDto.class);

        Optional<ContentEntity> contentEntityOptional =
                contentDao.findByContentId(transcodingStatusDto.getContentId());

        if (contentEntityOptional.isEmpty()) {
            log.error("ContentId not found : " + transcodingStatusDto.getContentId());
            return;
        }

        ContentEntity content = contentEntityOptional.get();

        if (transcodingStatusDto.isSuccess()) {

            content.setState(ContentState.LIVE.name());
            contentResourceDao.saveResource(
                    ContentResourceEntity.builder()
                            .contentId(content.getId())
                            .filePath(transcodingStatusDto.getTranscodedPath())
                            .format(ResourceFormat.TRANSCODED.name())
                            .build()
            );

        } else {
            content.setState(ContentState.ERROR.name());
        }
        contentDao.saveContent(content);
    }
}
