package com.swproject.hereforus.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupOutputDto {
    private String nickName;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate anniversary;

    // 클라이언트로에게 반환할 때 사용
    private String profileImg;
}
