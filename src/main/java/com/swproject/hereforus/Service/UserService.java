package com.swproject.hereforus.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Configuration
@PropertySource("classpath:env.properties")
@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${NAVER_CLIENT_ID}")
    private String naver_client_id;

    @Value("${NAVER_CLIENT_SECRET}")
    private String naver_client_secret;

    @Value("${NAVER_CALLBACK_URL}")
    private String naver_callback_url;


    public String fetchNaverUrl() {
        String baseUrl = "https://nid.naver.com/oauth2.0/authorize";

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", naver_client_id)
                .queryParam("state", "STATE_STRING")
                .queryParam("redirect_uri", naver_callback_url)
                .toUriString();

        return url;
    }


    public String CodeToToken(String code, String state) throws IOException {
        String baseUrl = "https://nid.naver.com/oauth2.0/token";

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", naver_client_id)
                .queryParam("client_secret", naver_client_secret)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        JsonNode rootNode = objectMapper.readTree(response);
        String accessToken = rootNode.path("access_token").asText();

        return accessToken;
    }

    public ResponseEntity<String> fetchNaverProfile(String token) {
        String apiUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        return response;
    }

    // 프로필 정보 가공
    // access token 생성 (이메일만 추출)
    // access token 발급
    // refresh token 생성
    // refresh 요청 시 access 갱신
    // email 확인하고 새로운 유저 등록 => 토큰 발급 or 기존 회원은 토큰 발급만 하는 * 인증 처리*
    // 로그아웃 시 토큰 삭제
}
