package com.swproject.hereforus.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class JwtDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
