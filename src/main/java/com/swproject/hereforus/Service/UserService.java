package com.swproject.hereforus.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Configuration
@PropertySource("classpath:env.properties")
@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;

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


    @ResponseBody
    public Object CodeToToken(String code, String state){
        String baseUrl = "https://nid.naver.com/oauth2.0/token";

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", naver_client_id)
                .queryParam("client_secret", naver_client_secret)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}
