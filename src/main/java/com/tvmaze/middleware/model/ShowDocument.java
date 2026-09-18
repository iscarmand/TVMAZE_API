package com.tvmaze.middleware.model;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author armand
 */
@Document(collection = "shows_cache")
public class ShowDocument {

    @Id
    private Long id;
    private Map<String, Object> data;

    // Índice TTL: MongoDB eliminará automáticamente el documento 24 horas (86400 s) después de su creación
    @Indexed(expireAfterSeconds = 86400)
    private Instant createdAt;

    public ShowDocument() {
    }

    public ShowDocument(Long id, Map<String, Object> data, Instant createdAt) {
        this.id = id;
        this.data = data;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
