package com.youtube.be.service.impl;

import com.youtube.be.dao.ContentDao;
import com.youtube.be.dao.ContentResourceDao;
import com.youtube.be.dto.TranscodingMessageDto;
import com.youtube.be.entity.ContentEntity;
import com.youtube.be.entity.ContentResourceEntity;
import com.youtube.be.enums.ResourceFormat;
import com.youtube.be.service.ContentService;
import com.youtube.be.service.QueueService;
import com.youtube.be.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentResourceDao contentResourceDao;

    @Autowired
    private StorageService storageService;

    @Autowired
    private QueueService queueService;


    public Object uploadVideo(MultipartFile file, String name) throws IOException {

        // make entry in db

        ContentEntity content = contentDao.saveContent(ContentEntity.builder()
                .title(name)
                .description("Test desc")
                .publisher("Hardik")
                .build());

        // get video id and upload to storage
        String rawFilePath = "raw/" + content.getId() + ".mkv";
        String destinationPath = "transcoded/" + content.getId();
        storageService.uploadFile(file, rawFilePath);

        // Save entry in content resource

        contentResourceDao.saveResource(ContentResourceEntity.builder()
                .contentId(content.getId())
                .filePath(rawFilePath)
                .format(ResourceFormat.RAW.name())
                .build());

        // trigger transcoding

        queueService.publishMessage("transcoding-topic", rawFilePath,
                TranscodingMessageDto.builder()
                        .rawFilePath(rawFilePath)
                        .destinationPath(destinationPath)
                        .build());

        // update status in DB

        // add transcoded path in db

        return null;
    }
}
