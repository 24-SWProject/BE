package com.swproject.hereforus.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@PropertySource("classpath:env.properties")
public class JwtProperties {
    @Value("${JWT_ISSUER}")
    private String issuer;

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${NAVER_CLIENT_ID}")
    private String naver_client_id;
}
