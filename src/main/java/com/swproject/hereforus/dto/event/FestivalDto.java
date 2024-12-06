package com.swproject.hereforus.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FestivalDto {
    @JsonProperty("mt20id")
    private String id;

    @JsonProperty("genrenm")
    private String category;

    @JsonProperty("prfnm")
    private String title;

    @JsonProperty("fcltynm")
    private String place;

    @JsonProperty("prfpdfrom")
    private String openDate;

    @JsonProperty("prfpdto")
    private String endDate;

    @JsonProperty("poster")
    private String poster;

    @JsonProperty("prfstate")
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
