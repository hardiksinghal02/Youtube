package com.youtube.be.dao;

import com.youtube.be.entity.ContentEntity;
import com.youtube.be.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentDao {

    @Autowired
    private ContentRepository contentRepository;

    public ContentEntity saveContent(ContentEntity contentEntity) {
        return contentRepository.save(contentEntity);
    }
}
