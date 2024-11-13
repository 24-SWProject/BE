package com.swproject.hereforus.config.jwt;

import com.swproject.hereforus.dto.UserDto;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessTokenFromHeader(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            authenticateUser(accessToken);
        } else {
            String refreshToken = getRefreshTokenFromCookies(request.getCookies());

            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                String userEmail = jwtTokenProvider.getUserEmail(refreshToken);

                // 새로운 Access Token 발급 후 헤더에 추가
                String newAccessToken = jwtTokenProvider.createAccessToken(userEmail);
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                authenticateUser(newAccessToken);
            } else if (refreshToken != null) {
                // Refresh Token이 유효하지 않은 경우 새로 발급
                String userEmail = jwtTokenProvider.getUserEmail(refreshToken);

                if (userEmail != null) {  // userEmail이 null인지 확인
                    String newAccessToken = jwtTokenProvider.createAccessToken(userEmail);
                    response.addHeader("Authorization", "Bearer " + newAccessToken);

                    // 리프레시 토큰 생성 및 쿠키에 설정
                    String newRefreshToken = jwtTokenProvider.createRefreshToken(userEmail);
                    Cookie refreshCookie = jwtTokenProvider.createCookie(newRefreshToken);
                    response.addCookie(refreshCookie);

                    authenticateUser(newAccessToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    // 쿠키에서 리프레시 토큰 추출
    private String getRefreshTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("refreshtoken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // Authorization 헤더에서 토큰 추출
    private String getAccessTokenFromHeader(HttpServletRequest request) {
        String headerValue = request.getHeader("Authorization");
        if (headerValue != null && headerValue.startsWith("Bearer ")) {
            return headerValue.substring("Bearer ".length());
        }
        return null;
    }

    // 접근 권한 설정
    private void authenticateUser(String token) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (userDetails != null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}


