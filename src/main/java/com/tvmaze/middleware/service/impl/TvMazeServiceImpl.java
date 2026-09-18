package com.tvmaze.middleware.service.impl;

import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import com.tvmaze.middleware.dto.external.TvMazeSearchItemDto;
import com.tvmaze.middleware.exception.ResourceNotFoundException;
import com.tvmaze.middleware.model.ShowDocument;
import com.tvmaze.middleware.repository.ShowRepository;
import com.tvmaze.middleware.service.TvMazeService;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author armand
 */
@Service
public class TvMazeServiceImpl implements TvMazeService {

    private final RestTemplate restTemplate;
    private final ShowRepository showRepository;
    
    private static final String TVMAZE_SEARCH_URL = "http://api.tvmaze.com/search/shows";
    private static final String TVMAZE_SHOW_BY_ID_URL = "https://api.tvmaze.com/shows/";

    public TvMazeServiceImpl(RestTemplate restTemplate, ShowRepository showRepository) {
        this.restTemplate = restTemplate;
        this.showRepository = showRepository;
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
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getShowById(Long showId) {
        // 1. Validar si existe en la base de datos de MongoDB (Caché)
        Optional<ShowDocument> cachedShow = showRepository.findById(showId);
        if (cachedShow.isPresent()) {
            return cachedShow.get().getData();
        }

        // 2. Si no se encuentra, consumir la API externa de TVMaze
        String url = TVMAZE_SHOW_BY_ID_URL + showId;
        try {
            Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);
            if (apiResponse != null) {
                // 3. Guardar el resultado en MongoDB antes de retornar
                ShowDocument showDocument = new ShowDocument(showId, apiResponse, Instant.now());
                showRepository.save(showDocument);
            }

            return apiResponse;
            
        }
        catch(HttpClientErrorException.NotFound e){
            throw new ResourceNotFoundException("No se encontró el show con ID: " + showId);
        }
    }
}
