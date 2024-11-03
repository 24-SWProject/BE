package com.swproject.hereforus.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDto {
    @JsonProperty("movieCd")
    private String id;

    @JsonProperty("movieNm")
    private String title;

    @JsonProperty("rank")
    private int rank;

    @JsonProperty("openDt")
    private String openDate;

    @JsonProperty("audiAcc")
    private String audience;
}

