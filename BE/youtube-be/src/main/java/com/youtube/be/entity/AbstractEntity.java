package com.youtube.be.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractEntity {

    @Id
    private UUID id;

    @PrePersist
    public void assignId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public String getId() {
        return id.toString();
    }
}
