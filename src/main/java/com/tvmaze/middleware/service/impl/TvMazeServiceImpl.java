package com.tvmaze.middleware.service.impl;

import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import com.tvmaze.middleware.dto.external.TvMazeSearchItemDto;
import com.tvmaze.middleware.service.TvMazeService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Collections;
import java.util.List;

/**
 * @author armand
 */
@Service
public class TvMazeServiceImpl implements TvMazeService {

    private final RestTemplate restTemplate;
    private static final String TVMAZE_SEARCH_URL = "http://api.tvmaze.com/search/shows";

    public TvMazeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ShowSearchResponseDto> searchShows(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(TVMAZE_SEARCH_URL)
                .queryParam("q", query)
                .toUriString();

        TvMazeSearchItemDto[] response = restTemplate.getForObject(url, TvMazeSearchItemDto[].class);

        if (response == null) {
            return Collections.emptyList();
        }

        return List.of(response).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private ShowSearchResponseDto mapToResponseDto(TvMazeSearchItemDto item) {
        if (item == null || item.getShow() == null) {
            return null;
        }

        var show = item.getShow();

        // Extraer canal: ya sea network.name o webChannel.name
        String channelName = null;
        if (show.getNetwork() != null && show.getNetwork().getName() != null) {
            channelName = show.getNetwork().getName();
        } else if (show.getWebChannel() != null && show.getWebChannel().getName() != null) {
            channelName = show.getWebChannel().getName();
        }

        return new ShowSearchResponseDto(
                show.getId(),
                show.getName(),
                channelName,
                show.getSummary(),
                show.getGenres(),
                Collections.emptyList()
        );
    }
}
