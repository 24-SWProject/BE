package com.swproject.hereforus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NonNull
    private String email;
    private String password;
    private String nickname;
    private String profileImg;
    private String birthYear;
    private String birthDate;

}


