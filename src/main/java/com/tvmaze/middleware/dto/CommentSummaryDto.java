package com.tvmaze.middleware.dto;

/**
 * @author armand
 */
public class CommentSummaryDto {

    private String comment;
    private Integer rating;

    public CommentSummaryDto() {
    }

    public CommentSummaryDto(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
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
}
