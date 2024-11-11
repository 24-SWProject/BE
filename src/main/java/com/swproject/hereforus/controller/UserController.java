package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.jwt.JwtTokenProvider;
import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/user/login")
    public void getCodeStatus(HttpServletResponse request, HttpServletResponse response) throws IOException {
        String loginUrl = userService.fetchNaverUrl();
        response.sendRedirect(loginUrl);
    }

    @ResponseBody
    @GetMapping("/naver/auth")
    public ResponseEntity<String> getToken(@RequestParam(value="code") String code, @RequestParam(value="state") String state) throws IOException {
        // Code를 사용해 토큰을 요청
        String token = userService.CodeToToken(code, state);

        // 토큰으로 네이버에서 사용자 프로필 조회
        UserDto profile = userService.fetchNaverProfile(token);

        // 유저 생성
        UserDto user = userService.createUser(profile);

        // 기존 유저 시 pass
        // 로그인 실패 시 처리

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);

        System.out.println(accessToken);

        return ResponseEntity.status(HttpStatus.OK).body("로그인이 완료되었습니다.");
    }

    @ResponseBody
    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());

        return "로그아웃 되었습니다.";
    }

    @ResponseBody
    @GetMapping("/test")
    public Object test(){
        return "성공";
    }
}