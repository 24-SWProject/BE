package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/login")
    public void getCodeStatus(HttpServletResponse request, HttpServletResponse response) throws IOException {
        String loginUrl = userService.fetchNaverUrl();
        response.sendRedirect(loginUrl);
    }

    @ResponseBody
    @GetMapping("/api/naver/auth")
    public Object getToken(@RequestParam(value="code") String code, @RequestParam(value="state") String state) throws IOException {
        // Code를 사용해 토큰을 요청
        String token = userService.CodeToToken(code, state);

        // 토큰으로 네이버에서 사용자 프로필 조회
        UserDto profile = userService.fetchNaverProfile(token);
        System.out.println(profile);

        Long userId = userService.createUser(profile);
        System.out.println(userId);

        // 저장된 사용자 ID와 프로필 JSON 반환
        return Map.of("userId", userId, "profile", profile);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());

        return "로그아웃 되었습니다.";
    }
}