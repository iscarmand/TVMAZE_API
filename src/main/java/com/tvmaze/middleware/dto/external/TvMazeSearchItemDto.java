package com.tvmaze.middleware.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author armand
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeSearchItemDto {

    private Double score;
    private TvMazeShowDto show;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public TvMazeShowDto getShow() {
        return show;
    }

    public void setShow(TvMazeShowDto show) {
        this.show = show;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TvMazeShowDto {

        private Long id;
        private String name;
        private String summary;
        private List<String> genres;
        private NetworkDto network;

        @JsonProperty("webChannel")
        private NetworkDto webChannel;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getGenres() {
            return genres;
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }

        public NetworkDto getNetwork() {
            return network;
        }

        public void setNetwork(NetworkDto network) {
            this.network = network;
        }

        public NetworkDto getWebChannel() {
            return webChannel;
        }

        public void setWebChannel(NetworkDto webChannel) {
            this.webChannel = webChannel;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkDto {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
