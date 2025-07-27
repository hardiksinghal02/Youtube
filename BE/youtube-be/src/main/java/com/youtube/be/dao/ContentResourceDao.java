package com.youtube.be.dao;

import com.youtube.be.entity.ContentResourceEntity;
import com.youtube.be.repository.ContentResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentResourceDao {

    @Autowired
    private ContentResourceRepository contentResourceRepository;

    public ContentResourceEntity saveResource(ContentResourceEntity contentResourceEntity) {
        return contentResourceRepository.save(contentResourceEntity);
    }
}
