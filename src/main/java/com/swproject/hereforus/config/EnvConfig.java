package com.swproject.hereforus.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:env.properties")
public class EnvConfig {
    @Value("${FESTIVAL_BASEURL}")
    private String festivalBaseUrl;

    @Value("${MOVIE_BASEURL}")
    private String movieBaseUrl;

    @Value("${MOVIE_KEY}")
    private String movieKey;

    @Value("${WEATHER_BASEURL}")
    private String weatherBaseUrl;

    @Value("${NAVER_CLIENT_ID}")
    private String naverClientId;

    @Value("${NAVER_CLIENT_SECRET}")
    private String naverClientSecret;

    @Value("${NAVER_CALLBACK_URL}")
    private String naverCallbackUrl;

    @Value("${JWT_ISSUER}")
    private String JwtIssuer;

    @Value("${JWT_SECRET_KEY}")
    private String JwtSecretKey;

    @Value("${DATABASE_URL}")
    private String DatabaseUrl;

    @Value("${DATABASE_USERNAME}")
    private String DatabaseUsername;

    @Value("${DATABASE_PASSWORD}")
    private String DatabasePassword;
}
