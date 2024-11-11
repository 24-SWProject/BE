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
    private String FestivalBaseUrl;

    @Value("${MOVIE_BASEURL}")
    private String MovieBaseUrl;

    @Value("${MOVIE_KEY}")
    private String MovieKey;

    @Value("${WEATHER_BASEURL}")
    private String WeatherBaseUrl;

    @Value("${PERFORMANCE_BASEURL}")
    private String PerformanceBaseUrl;

    @Value("${NAVER_CLIENT_ID}")
    private String NaverClientId;

    @Value("${NAVER_CLIENT_SECRET}")
    private String NaverClientSecret;

    @Value("${NAVER_CALLBACK_URL}")
    private String NaverCallbackUrl;

    @Value("${JWT_EXPIRATION_TIME}")
    private Long JwtExpirationTime;

    @Value("${JWT_SECRET_KEY}")
    private String JwtSecretKey;

    @Value("${DATABASE_URL}")
    private String DatabaseUrl;

    @Value("${DATABASE_USERNAME}")
    private String DatabaseUsername;

    @Value("${DATABASE_PASSWORD}")
    private String DatabasePassword;


}
