package com.swproject.hereforus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final EnvConfig envConfig;
    private final UserRepository userRepository;


    public String fetchNaverUrl() {
        String baseUrl = "https://nid.naver.com/oauth2.0/authorize";

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", envConfig.getNaverClientId())
                .queryParam("state", "STATE_STRING")
                .queryParam("redirect_uri", envConfig.getNaverCallbackUrl())
                .toUriString();

        return url;
    }


    public String CodeToToken(String code, String state) throws IOException {
        String baseUrl = "https://nid.naver.com/oauth2.0/token";

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", envConfig.getNaverClientId())
                .queryParam("client_secret", envConfig.getNaverClientSecret())
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode accessToken = rootNode.path("access_token");

        return objectMapper.writeValueAsString(accessToken);
    }

    public UserDto fetchNaverProfile(String token) throws IOException {
        String apiUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode responseNode = rootNode.path("response");

        return UserDto.builder()
                .email(responseNode.path("email").asText())
                .nickname(responseNode.path("nickname").asText())
                .profileImg(responseNode.path("profile_image").asText())
                .birthYear(responseNode.path("birthyear").asText())
                .birthDate(responseNode.path("birthday").asText())
                .build();
    }


    public Long createUser(UserDto userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .profileImg(userInfo.getProfileImg())
                .birthYear(userInfo.getBirthYear())
                .birthDate(userInfo.getBirthDate())
                .build();

        return userRepository.save(user).getId();  // 저장 후 ID 반환
    }

        public static void deleteUser() {

    }

    public static void createAccessToken() {
        // access token 생성 (이메일만 추출)
    }

    public static void createRefreshToken() {
        // refresh token 생성
    }

    public static void updateAccessToken() {
        // refresh 요청 시 access 갱신
    }

    public static void deleteAccessToken() {
        // 로그아웃 시 토큰 삭제
    }



    // email 확인하고 새로운 유저 등록 => 토큰 발급 or 기존 회원은 토큰 발급만 하는 * 인증 처리 *
}
