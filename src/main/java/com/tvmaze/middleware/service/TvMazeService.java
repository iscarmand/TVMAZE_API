package com.tvmaze.middleware.service;
import com.tvmaze.middleware.dto.CommentRequestDto;
import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import com.tvmaze.middleware.model.CommentDocument;
import java.util.List;
import java.util.Map;

/**
 * @author armand
 */
public interface TvMazeService {
    List<ShowSearchResponseDto> searchShows(String query);
    Map<String, Object> getShowById(Long showId);
    CommentDocument addComment(Long showId, CommentRequestDto commentRequestDto);
    List<CommentDocument> getCommentsByShowId(Long showId);
}
