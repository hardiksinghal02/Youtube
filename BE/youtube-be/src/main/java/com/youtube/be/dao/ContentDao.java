package com.youtube.be.dao;

import com.youtube.be.entity.ContentEntity;
import com.youtube.be.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContentDao {

    @Autowired
    private ContentRepository contentRepository;

    public ContentEntity saveContent(ContentEntity contentEntity) {
        return contentRepository.save(contentEntity);
    }

    public Optional<ContentEntity> findByContentId(String contentId) {
        return contentRepository.findById(contentId);
    }
}
