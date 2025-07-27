package com.youtube.be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "content")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ContentEntity extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    private String description;

    private String publisher;
}
