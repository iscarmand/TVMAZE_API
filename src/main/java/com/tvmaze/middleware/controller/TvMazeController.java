package com.tvmaze.middleware.controller;
import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import com.tvmaze.middleware.service.TvMazeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
/**
 * @author armand
 */
@RestController
@RequestMapping("/api/v1/shows")
public class TvMazeController {
    private final TvMazeService tvMazeService;
     public TvMazeController(TvMazeService tvMazeService) {
        this.tvMazeService = tvMazeService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShowSearchResponseDto>> searchShows(@RequestParam("search_query") String searchQuery) {
        List<ShowSearchResponseDto> result = tvMazeService.searchShows(searchQuery);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{show_id}")
    public ResponseEntity<Map<String, Object>> getShowById(@PathVariable("show_id") Long showId) {
        Map<String, Object> show = tvMazeService.getShowById(showId);
        return ResponseEntity.ok(show);
    }
}