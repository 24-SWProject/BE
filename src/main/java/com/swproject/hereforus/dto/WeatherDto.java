package com.swproject.hereforus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDto {
    @JsonProperty("main")
    private String main;

    @JsonProperty("description")
    private String description; // 날씨 설명

    @JsonProperty("icon")
    private String icon; // 날씨 아이콘 코드

    @JsonProperty("temperature")
    private double temperature; // 현재 온도

    @JsonProperty("feelsLike")
    private double feelsLike; // 체감 온도

    @JsonProperty("tempMin")
    private double tempMin; // 최저 온도

    @JsonProperty("tempMax")
    private double tempMax; // 최고 온도

    @JsonProperty("pressure")
    private int pressure; // 기압

    @JsonProperty("humidity")
    private int humidity; // 습도
}

