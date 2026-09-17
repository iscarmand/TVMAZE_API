package com.tvmaze.middleware.service;
import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import com.tvmaze.middleware.dto.external.TvMazeSearchItemDto;
import com.tvmaze.middleware.service.impl.TvMazeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * @author armand
 */
public class TvMazeServiceImplTest {
private RestTemplate restTemplate;
    private TvMazeServiceImpl tvMazeService;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        tvMazeService = new TvMazeServiceImpl(restTemplate);
    }

    @Test
    void searchShows_ShouldReturnMappedList_WhenApiReturnsData() {
        // Given
        TvMazeSearchItemDto item = new TvMazeSearchItemDto();
        TvMazeSearchItemDto.TvMazeShowDto show = new TvMazeSearchItemDto.TvMazeShowDto();
        show.setId(100L);
        show.setName("Batman The Dark Knight");
        show.setGenres(List.of("Drama", "Science-Fiction"));
        
        TvMazeSearchItemDto.NetworkDto network = new TvMazeSearchItemDto.NetworkDto();
        network.setName("DC Comics");
        show.setNetwork(network);

        item.setShow(show);

        TvMazeSearchItemDto[] mockResponse = new TvMazeSearchItemDto[]{item};

        Mockito.when(restTemplate.getForObject(anyString(), eq(TvMazeSearchItemDto[].class)))
                .thenReturn(mockResponse);

        // Cuando (When)
        List<ShowSearchResponseDto> result = tvMazeService.searchShows("girls");

        // Entonces (Then)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Batman The Dark Knight", result.get(0).getName());
        assertEquals("DC Comics", result.get(0).getChannel());
    }
}