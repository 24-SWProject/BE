package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

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
        String token = userService.CodeToToken(code, state);
        String profile = userService.fetchNaverProfile(token);

        return profile;
    }

    @PostMapping("/user")
    public String signup(UserDto user){
        return "";
    }
}