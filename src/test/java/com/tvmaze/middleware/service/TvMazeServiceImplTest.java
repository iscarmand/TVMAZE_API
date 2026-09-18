package com.tvmaze.middleware.service;
import com.tvmaze.middleware.dto.ShowSearchResponseDto;
import com.tvmaze.middleware.dto.external.TvMazeSearchItemDto;
import com.tvmaze.middleware.model.ShowDocument;
import com.tvmaze.middleware.repository.ShowRepository;
import com.tvmaze.middleware.service.impl.TvMazeServiceImpl;
import java.time.Instant;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author armand
 */
public class TvMazeServiceImplTest {
    private RestTemplate restTemplate;
    private TvMazeServiceImpl tvMazeService;
    private ShowRepository showRepository;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        showRepository = Mockito.mock(ShowRepository.class);
        tvMazeService = new TvMazeServiceImpl(restTemplate,showRepository);
        
    }

    @Test
    @DisplayName("getShowById - Debería retornar desde la Caché (Mongo) si el ID ya existe")
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
        List<ShowSearchResponseDto> result = tvMazeService.searchShows("Batman");

        // Entonces (Then)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Batman The Dark Knight", result.get(0).getName());
        assertEquals("DC Comics", result.get(0).getChannel());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(TvMazeSearchItemDto[].class));
    }
    @Test
    @DisplayName("getShowById - Debería retornar desde la Caché (Mongo) si el ID ya existe")
    void getShowById_ShouldReturnFromCache_WhenShowExistsInMongo() {
        // Arrange
        Long showId = 1L;
        Map<String, Object> cachedData = new HashMap<>();
        cachedData.put("id", showId);
        cachedData.put("name", "Batman The Dark Knight");

        ShowDocument cachedDocument = new ShowDocument(showId, cachedData, Instant.now());

        when(showRepository.findById(showId)).thenReturn(Optional.of(cachedDocument));

        // Act
        Map<String, Object> result = tvMazeService.getShowById(showId);

        // Assert
        assertNotNull(result);
        assertEquals("Batman The Dark Knight", result.get("name"));
        verify(showRepository, times(1)).findById(showId);
        verifyNoInteractions(restTemplate); // Garantiza que NO consume la API externa
        verify(showRepository, never()).save(any());
    }

    @Test
    @DisplayName("getShowById - Debería consumir API externa, asignar timestamp TTL y guardar en Mongo si NO existe en Caché")
    void getShowById_ShouldFetchFromApiAndSaveInMongo_WhenCacheMiss() {
        // Arrange
        Long showId = 1L;
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("id", showId);
        apiResponse.put("name", "Batman The Dark Knight");

        when(showRepository.findById(showId)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        // Act
        Map<String, Object> result = tvMazeService.getShowById(showId);

        // Assert
        assertNotNull(result);
        assertEquals("Batman The Dark Knight", result.get("name"));
        
        // Capturamos el objeto guardado para verificar que se generó la fecha para el TTL
        ArgumentCaptor<ShowDocument> showCaptor = ArgumentCaptor.forClass(ShowDocument.class);
        verify(showRepository, times(1)).save(showCaptor.capture());
        ShowDocument savedDocument = showCaptor.getValue();
        assertEquals(showId, savedDocument.getId());
        assertNotNull(savedDocument.getCreatedAt(), "El atributo createdAt debe ser inicializado para que Mongo aplique el TTL");
        
        verify(showRepository, times(1)).findById(showId);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));
        verify(showRepository, times(1)).save(any(ShowDocument.class)); // Garantiza persistencia
    }
}