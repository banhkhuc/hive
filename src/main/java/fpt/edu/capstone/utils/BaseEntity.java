package fpt.edu.capstone.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "createdAt")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "updatedAt")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BaseEntity create() {
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
        return this;
    }

    public BaseEntity update() {
        setUpdatedAt(LocalDateTime.now());
        return this;
    }
}