package com.swproject.hereforus.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDto {
    private String title;
    private String openDate;
    private Integer audiAcc;
    private String poster;
    private Integer runtime;
    private String genre;
    private List<String> actors;

    public MovieDto(String title, String openDate, Integer audiAcc, String poster, Integer runtime, String genre, List<String> actors) {
        this.title = title;
        this.openDate = openDate;
        this.audiAcc = audiAcc;
        this.poster = poster;
        this.runtime = runtime;
        this.genre = genre;
        this.actors = actors;
    }
}