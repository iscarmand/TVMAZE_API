package com.tvmaze.middleware.service;
import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import java.util.List;
import java.util.Map;

/**
 * @author armand
 */
public interface TvMazeService {
    List<ShowSearchResponseDto> searchShows(String query);
    Map<String, Object> getShowById(Long showId);
}
