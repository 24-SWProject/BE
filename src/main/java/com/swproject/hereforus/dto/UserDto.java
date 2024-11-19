package com.swproject.hereforus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NonNull
    private String email;
    private String password;
    private String nickname;
    private String profileImg;
    private String birthYear;
    private String birthDate;
}


