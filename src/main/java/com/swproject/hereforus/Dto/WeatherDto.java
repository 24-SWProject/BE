package com.swproject.hereforus.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDto {
    private String main;
    private String description; // 날씨 설명
    private String icon; // 날씨 아이콘 코드
    private double temperature; // 현재 온도
    private double feelsLike; // 체감 온도
    private double tempMin; // 최저 온도
    private double tempMax; // 최고 온도
    private int pressure; // 기압
    private int humidity; // 습도
}

