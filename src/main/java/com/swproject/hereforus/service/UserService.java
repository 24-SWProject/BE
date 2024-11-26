package com.swproject.hereforus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swproject.hereforus.config.EnvConfig;
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
    private final ModelMapper modelMapper;
    private final UserDetailService userDetailService;


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
                .birthDate(responseNode.path("birthday").asText())
                .build();
    }

    // User 생성
    @Transactional
    public UserDto createUser(UserDto userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .birthDate(userInfo.getBirthDate())
                .build();

        // Group 생성
        Group group = Group.builder()
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

    // 인가코드를 토큰으로 변환
    // 토큰을 jwt로 변환
    // 프로필 이미지 생성

}
