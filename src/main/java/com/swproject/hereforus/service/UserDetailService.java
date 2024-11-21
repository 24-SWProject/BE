package com.swproject.hereforus.service;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getAuthenticatedUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            System.out.println(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(HttpStatus.FORBIDDEN, "사용자 인증에 실패하였습니다.");
        }
    }
}
