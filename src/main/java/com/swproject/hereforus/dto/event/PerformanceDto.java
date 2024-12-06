package com.swproject.hereforus.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class PerformanceDto {

    @JsonProperty("mt20id")
    private String id;

    @JsonProperty("genrenm") // 공연 장르
    private String category;

    @JsonProperty("prfnm") // 공연 이름
    private String title;

    @JsonProperty("fcltynm") // 공연 장소
    private String place;

    @JsonProperty("prfpdfrom") // 공연 시작 날짜
    private String openDate;

    @JsonProperty("prfpdto") // 공연 종료 날짜
    private String endDate;

    @JsonProperty("poster") // 공연 포스터 정보
    private String poster;

    @JsonProperty("prfstate") // 공연 종료 날짜
    private String state;

    private String registerLink;

    private String useFee;

    private String useAge;

    private String useTime;

    @Transient
    private boolean bookmarked;

    @Transient
    private String type;
}

