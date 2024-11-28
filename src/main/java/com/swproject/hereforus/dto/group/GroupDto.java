package com.swproject.hereforus.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickName;

    @NotBlank(message = "기념일을 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate anniversary;

    // 클라이언트로부터 받을 때 사용
    @NotBlank(message = "프로필 사진을 등록해주세요.")
    private MultipartFile profileImg;

}
