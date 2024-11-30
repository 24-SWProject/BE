package com.swproject.hereforus.service;

import com.swproject.hereforus.config.EnvConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Service
public class RecommendService {
    private final EnvConfig envConfig;
    private final RestTemplate restTemplate;

    public String fetchRecommendByAI(Map<String, List<String>> requestBody) {
        String url = UriComponentsBuilder
                .fromUriString(envConfig.getAIUrl())
                .toUriString();

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Content-Type: application/json 설정

        // 요청 본문과 헤더를 함께 설정
        HttpEntity<Map<String, List<String>>> entity = new HttpEntity<>(requestBody, headers);

        // POST 요청 전송
        String response = restTemplate.postForObject(url, entity, String.class);

        return response;
    }

}
