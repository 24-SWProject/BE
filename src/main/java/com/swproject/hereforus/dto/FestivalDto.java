package com.swproject.hereforus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
    private String rgstLink;

    @JsonProperty("MAIN_IMG")
    private String mainImg;

    @JsonProperty("RGSTDATE")
    private String rgstDate;

    @JsonProperty("STRTDATE")
    private String strtDate;

    @JsonProperty("END_DATE")
    private String endDate;

    @JsonProperty("LOT")
    private String longitude;

    @JsonProperty("LAT")
    private String latitude;
}
