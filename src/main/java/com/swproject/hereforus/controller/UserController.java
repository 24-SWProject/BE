package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.jwt.JwtTokenProvider;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.dto.JwtDto;
import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.UserRepository;
import com.swproject.hereforus.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Optional;

@Tag(name = "User", description = "회원 관련 REST API에 대한 명세를 제공합니다. ")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    @Operation(
            summary = "네이버 로그인 및 회원가입",
            description = "네이버 소셜 로그인을 통해 로그인 및 회원가입을 할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = JwtDto.class))),
                    @ApiResponse(responseCode = "500", description = "로그인 실패", content = @Content(schema = @Schema(example = "{\"error\":\"로그인 중 문제가 발생했습니다. 다시 시도해 주세요.\"}")))
            }
    )
    @GetMapping("/user/login")
    public void getCodeStatus(HttpServletResponse response) throws IOException {
        String loginUrl = userService.fetchNaverUrl();
        response.sendRedirect(loginUrl);
    }

    @Hidden
    @GetMapping(value = "/naver/auth", produces = "application/json")
    public ResponseEntity<?> getToken(@RequestParam(value="code") String code, @RequestParam(value="state") String state, HttpServletResponse httpServletResponse) throws IOException {
        try {
            // Code를 사용해 토큰을 요청
            String token = userService.CodeToToken(code, state);
            // 토큰으로 네이버에서 사용자 프로필 조회
            UserDto profile = userService.fetchNaverProfile(token);

            System.out.println(profile);
            System.out.println(profile.getEmail());

            Optional<User> existingUser = userRepository.findByEmail(profile.getEmail());
            if (existingUser.isPresent()) {
                System.out.println("User already exists: " + existingUser.get());
            } else {
                UserDto savedUser = userService.createUser(profile);
                System.out.println("Saved user: " + savedUser);
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
            ErrorDto errorResponse = new ErrorDto("로그인 중 문제가 발생했습니다. 다시 시도해 주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Hidden
    @GetMapping(value = "/user/logout", produces = "application/json")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }
}