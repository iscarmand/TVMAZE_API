package com.tvmaze.middleware.service.impl;

import com.tvmaze.middleware.dto.CommentRequestDto;
import com.tvmaze.middleware.dto.CommentSummaryDto;
import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import com.tvmaze.middleware.dto.external.TvMazeSearchItemDto;
import com.tvmaze.middleware.exception.ResourceNotFoundException;
import com.tvmaze.middleware.model.CommentDocument;
import com.tvmaze.middleware.model.ShowDocument;
import com.tvmaze.middleware.repository.CommentRepository;
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
    private final CommentRepository commentRepository;
    
    private static final String TVMAZE_SEARCH_URL = "http://api.tvmaze.com/search/shows";
    private static final String TVMAZE_SHOW_BY_ID_URL = "https://api.tvmaze.com/shows/";

    public TvMazeServiceImpl(RestTemplate restTemplate, ShowRepository showRepository, CommentRepository commentRepository) {
        this.restTemplate = restTemplate;
        this.showRepository = showRepository;
        this.commentRepository = commentRepository;
        
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
        
        // Buscar comentarios existentes en MongoDB para este show
        List<CommentDocument> commentDocs = commentRepository.findByShowId(show.getId());
    
        // Mapear la lista de documentos al DTO requerido (comment y rating)
        List<CommentSummaryDto> comments = commentDocs.stream()
                .map(c -> new CommentSummaryDto(c.getComment(), c.getRating()))
                .toList();

        return new ShowSearchResponseDto(
                show.getId(),
                show.getName(),
                channelName,
                show.getSummary(),
                show.getGenres(),
                comments
                //Collections.emptyList()
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
    
    @Override
    public CommentDocument addComment(Long showId, CommentRequestDto commentRequest) {
        // Validar que el show exista antes de comentar
        getShowById(showId);

        CommentDocument commentDocument = new CommentDocument(
                showId,
                commentRequest.getComment(),
                commentRequest.getRating()
        );

        return commentRepository.save(commentDocument);
    }

    @Override
    public List<CommentDocument> getCommentsByShowId(Long showId) {
        return commentRepository.findByShowId(showId);
    }
}
