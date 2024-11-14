package com.swproject.hereforus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FestivalDto {

    @JsonProperty("GUNAME")
    private String guName;

    @JsonProperty("TITLE")
    private String title;

    @JsonProperty("PLACE")
    private String place;

    @JsonProperty("USE_TRGT")
    private String useTrgt;

    @JsonProperty("USE_FEE")
    private String useFee;

    @JsonProperty("ORG_LINK")
    private String registerLink;

    @JsonProperty("MAIN_IMG")
    private String poster;

    @JsonProperty("RGSTDATE")
    private String registerDate;

    @JsonProperty("STRTDATE")
    private String openDate;

    @JsonProperty("END_DATE")
    private String endDate;

    @JsonProperty("LOT")
    private String gpsY;

    @JsonProperty("LAT")
    private String gpsX;
}
