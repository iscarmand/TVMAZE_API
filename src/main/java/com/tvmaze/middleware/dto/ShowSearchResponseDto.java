package com.tvmaze.middleware.dto;
import java.util.List;

/**
 * @author armand
 */
public class ShowSearchResponseDto {
    private Long id;
    private String name;
    private String channel;
    private String summary;
    private List<String> genres;    
public ShowSearchResponseDto() {}

    public ShowSearchResponseDto(Long id, String name, String channel, String summary, List<String> genres, List<Object> comments) {
        this.id = id;
        this.name = name;
        this.channel = channel;
        this.summary = summary;
        this.genres = genres;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

}
