package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.jwt.JwtTokenProvider;
import com.swproject.hereforus.dto.JwtDto;
import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.repository.UserRepository;
import com.swproject.hereforus.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/login")
    public void getCodeStatus(HttpServletResponse response) throws IOException {
        String loginUrl = userService.fetchNaverUrl();
        response.sendRedirect(loginUrl);
    }

    @GetMapping(value = "/naver/auth", produces = "application/json")
    public ResponseEntity<JwtDto> getToken(@RequestParam(value="code") String code, @RequestParam(value="state") String state, HttpServletResponse httpServletResponse) throws IOException {
        try {
            // Code를 사용해 토큰을 요청
            String token = userService.CodeToToken(code, state);
            // 토큰으로 네이버에서 사용자 프로필 조회
            UserDto profile = userService.fetchNaverProfile(token);

            System.out.println(profile);
            System.out.println(profile.getEmail());

            if (userRepository.findByEmail(profile.getEmail()) == null) {
                userService.createUser(profile);
            }

            // 액세스 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(profile.getEmail());
            httpServletResponse.addHeader("Authorization", "Bearer " + accessToken);

            // 리프레시 토큰 생성
            String refreshToken = jwtTokenProvider.createRefreshToken(profile.getEmail());
            Cookie refreshCookie = jwtTokenProvider.createCookie(refreshToken);
            httpServletResponse.addCookie(refreshCookie);

            // JwtDto 객체 생성
            JwtDto jwtDto = new JwtDto("Bearer", accessToken, refreshToken);

            return ResponseEntity.ok(jwtDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/user/logout", produces = "application/json")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }
}