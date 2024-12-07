package com.swproject.hereforus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Configuration
@PropertySource("classpath:env.properties")
@RequiredArgsConstructor
@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final EnvConfig envConfig;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserDetailService userDetailService;
    private final GroupService groupService;

    /** 네이버 로그인 */
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

    public String CodeToTokenByNaver(String code, String state) throws IOException {
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
        String token = rootNode.path("access_token").asText();

        return token;
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
                .birthDate(responseNode.path("birthday").asText())
                .build();
    }

    /** 카카오 로그인 */
    public String fetchKakaoUrl() {
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("client_id", envConfig.getKakaoClientId())
                .queryParam("redirect_uri", envConfig.getKakaoCallbackUrl())
                .queryParam("response_type", "code")
                .toUriString();

        return url;
    }

    public String CodeToTokenByKakao(String code) throws IOException {
        String baseUrl = "https://kauth.kakao.com/oauth/token";

        // Headers 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // Body 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", envConfig.getKakaoClientId());
        body.add("redirect_uri", envConfig.getKakaoCallbackUrl());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);

        // token 추출
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String token = rootNode.path("access_token").asText();

        return token;
    }

    public UserDto fetchKakaoProfile(String token) throws IOException {
        String apiUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();

        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode kakaoAccount = rootNode.path("kakao_account");

        return UserDto.builder()
                .email(kakaoAccount.path("email").asText())
                .nickname(kakaoAccount.path("nickname").asText(null))
                .build();
    }

    // 카카오 액세스 토큰 저장
    public void saveKakaoAccessToken(String email, String kakaoAccessToken) {
        Optional<User> user = userRepository.findByEmail(email);
        user.get().setAccessToken(kakaoAccessToken);
        user.get().setSocialType("kakao");
        userRepository.save(user.get());
    }

    // 카카오 액세스 토큰 조회
    public String getKakaoAccessToken(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.get().getAccessToken();
    }

    // 카카오 연결 끊기
    public void unlinkKakao(String email) {
        String accessToken = getNaverAccessToken(email);

        // 카카오 연결 끊기 API 호출
        String apiUrl = "https://kapi.kakao.com/v1/user/unlink";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
    }

    // 네이버 액세스 토큰 저장
    public void saveNaverAccessToken(String email, String naverAccessToken) {
        Optional<User> user = userRepository.findByEmail(email);
        user.get().setAccessToken(naverAccessToken);
        user.get().setSocialType("naver");
        userRepository.save(user.get());
    }

    // 네이버 액세스 토큰 조회
    public String getNaverAccessToken(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.get().getAccessToken();
    }

    // 네이버 연결 끊기
    public void unlinkNaver(String email) {
        String accessToken = getNaverAccessToken(email);

        // 네이버 연결 끊기 API 호출
        String apiUrl = "https://nid.naver.com/oauth2.0/token";
        UriComponents url = UriComponentsBuilder
                .fromUriString(apiUrl)
                .queryParam("client_id", envConfig.getNaverClientId())
                .queryParam("client_secret", envConfig.getNaverClientSecret())
                .queryParam("access_token", accessToken)
                .queryParam("grant_type", "delete")
                .encode()
                .build();

        restTemplate.getForObject(url.toUri(), String.class);
    }

    // User 생성
    @Transactional
    public UserDto createUser(UserDto userInfo) throws IOException {

        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .birthDate(userInfo.getBirthDate())
                .build();

        // Group 생성
        Group group = Group.builder()
                .id(groupService.generateCode())
                .inviter(user)
                .build();
        user.setGroup(group);


        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDto.class);
    }

    // User 탈퇴
    public void withdrawUser() {
        User user = userDetailService.getAuthenticatedUserId();
        userRepository.deleteById(user.getId());
    }

}
