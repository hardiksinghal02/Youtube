package com.youtube.be.repository;

import com.youtube.be.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<ContentEntity, String> {
}
