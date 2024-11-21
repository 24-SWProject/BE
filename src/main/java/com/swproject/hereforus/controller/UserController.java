package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.jwt.JwtTokenProvider;
import com.swproject.hereforus.dto.ErrorDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@Tag(name = "User", description = "회원 관련 REST API에 대한 명세를 제공합니다. ")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Operation(
            summary = "네이버 로그인 및 회원가입",
            description = "네이버 소셜 로그인 기능을 사용해 간편하게 로그인하거나 새로운 계정을 생성할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(schema = @Schema(example = "{\"message\":\"네이버 로그인이 완료되었습니다.\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }
    )
    @GetMapping("/user/login")
    public void getCodeStatus(HttpServletResponse response) throws IOException {
        String loginUrl = userService.fetchNaverUrl();
        response.sendRedirect(loginUrl);
    }

    @Hidden
    @GetMapping(value = "/naver/auth", produces = "application/json")
    public ResponseEntity<?> getToken(@RequestParam(value = "code") String code, @RequestParam(value = "state") String state, HttpServletResponse httpServletResponse) throws IOException {
        try {
            // Code를 사용해 토큰을 요청
            String token = userService.CodeToToken(code, state);
            // 토큰으로 네이버에서 사용자 프로필 조회
            UserDto profile = userService.fetchNaverProfile(token);

            System.out.println(profile);
            System.out.println(profile.getEmail());

            // 유저 중복 확인
            Optional<User> existingUser = userRepository.findByEmail(profile.getEmail());
            if (!existingUser.isPresent()) {
                userService.createUser(profile);
            }

            // 액세스 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(profile.getEmail());
            httpServletResponse.addHeader("Authorization", "Bearer " + accessToken);

            // 리프레시 토큰 생성
            String refreshToken = jwtTokenProvider.createRefreshToken(profile.getEmail());
            Cookie refreshCookie = jwtTokenProvider.createCookie(refreshToken);
            httpServletResponse.addCookie(refreshCookie);

            return ResponseEntity.ok("네이버 로그인이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(
            summary = "로그아웃",
            description = "JWT 방식으로 인증된 사용자의 로그아웃을 처리합니다. 클라이언트가 보유한 액세스 및 리프레시 토큰을 삭제하여 로그아웃을 완료합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그아웃 성공",
                            content = @Content(schema = @Schema(example = "{\"message\":\"로그아웃이 완료되었습니다.\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }
    )
    @GetMapping(value = "/user/logout", produces = "application/json")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("로그아웃이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "회원 탈퇴를 요청하면 계정 정보와 연결된 그룹 데이터가 함께 삭제(소프트 삭제)됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 탈퇴 성공",
                            content = @Content(schema = @Schema(example = "{\"message\":\"회원 탈퇴가 성공적으로 처리되었습니다.\"}"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }
    )
    @DeleteMapping("user")
    public ResponseEntity<?> deleteUser() {
        try {
            userService.withdrawUser();
            return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}