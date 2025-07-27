package com.youtube.be.repository;

import com.youtube.be.entity.ContentResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentResourceRepository extends JpaRepository<ContentResourceEntity, String> {
}
