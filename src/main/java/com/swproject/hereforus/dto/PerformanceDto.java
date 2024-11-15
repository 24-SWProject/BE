package com.swproject.hereforus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformanceDto {
    @JsonProperty("mt20id") // 공연 id
    private String id;

    @JsonProperty("prfnm") // 공연 이름
    private String title;

    @JsonProperty("prfpdfrom") // 공연 시작 날짜
    private String openDate;

    @JsonProperty("prfpdto") // 공연 종료 날짜
    private String endDate;

    @JsonProperty("prfstate") // 공연 종료 날짜
    private String state;

    @JsonProperty("fcltynm") // 공연 장소
    private String place;

    @JsonProperty("poster") // 공연 포스터 정보
    private String poster;

    @JsonProperty("genrenm") // 공연 장르
    private String category;
}

