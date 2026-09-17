package com.tvmaze.middleware.service;
import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import java.util.List;

/**
 * @author armand
 */
public interface TvMazeService {
    List<ShowSearchResponseDto> searchShows(String query);
}
