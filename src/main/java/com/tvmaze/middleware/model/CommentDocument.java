package com.tvmaze.middleware.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author armand
 */
@Document(collection = "comments")
public class CommentDocument {

    @Id
    private String id;
    private Long showId;
    private String comment;
    private Integer rating;
    // Activa la política de expiración TTL en Mongo Atlas
    @Indexed(expireAfterSeconds = 86400)
    private Instant createdAt;

    public CommentDocument() {
    }

    public CommentDocument(Long showId, String comment, Integer rating) {
        this.showId = showId;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
