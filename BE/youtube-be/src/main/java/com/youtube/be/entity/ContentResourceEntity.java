package com.youtube.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "content_resources")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ContentResourceEntity extends AbstractEntity {

    @Column(nullable = false, name = "content_id")
    private String contentId;

    @Column(nullable = false, name = "file_path")
    private String filePath;

    @Column(nullable = false)
    private String format;
}
