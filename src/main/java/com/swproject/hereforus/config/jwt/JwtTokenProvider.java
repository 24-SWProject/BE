package com.swproject.hereforus.config.jwt;

import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Getter
@Setter
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpTime;
    private EnvConfig envConfig;

    // secret key 저장
    public JwtTokenProvider(EnvConfig envConfig) {
        this.envConfig = envConfig;
        byte[] keyBytes = java.util.Base64.getDecoder().decode(envConfig.getJwtSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = envConfig.getJwtExpirationTime();
    }

    // access token 생성
    public String createAccessToken(UserDto user) {
        return createToken(user, accessTokenExpTime);
    }

    // jwt 생성
    private String createToken(UserDto user, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("email", user.getEmail());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // token에서 user email 추출
    public String getUserEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    // jwt Claims 추출
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // jwt 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

}
