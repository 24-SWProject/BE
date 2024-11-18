package com.swproject.hereforus.config;

import com.swproject.hereforus.config.jwt.JwtAuthenticationFilter;
import com.swproject.hereforus.config.jwt.JwtTokenProvider;
import com.swproject.hereforus.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Arrays;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserDetailService userDetailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(userDetailService, jwtTokenProvider);
    }


    private static final String[] AUTH_WHITELIST = {
            "/api/**",
            "/recommend/**",
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    // 특정 http 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //CSRF, CORS
        http.csrf((csrf) -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.httpBasic(HttpBasicConfigurer::disable);
        http.formLogin((formlogin) -> formlogin.disable());

        //세션 관리 상태 없음으로 구성
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        //JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(new JwtAuthenticationFilter(userDetailService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        // 권한 규칙 작성
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(userDetailService);
        return authManagerBuilder.build();
    }
}
