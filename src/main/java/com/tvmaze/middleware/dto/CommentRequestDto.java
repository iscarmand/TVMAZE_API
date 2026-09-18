package com.tvmaze.middleware.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author armand
 */
public class CommentRequestDto {
    @NotBlank(message = "El comentario no puede estar vacío")
    private String comment;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 0, message = "La calificación mínima es 0")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    public CommentRequestDto() {}

    public CommentRequestDto(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
    
