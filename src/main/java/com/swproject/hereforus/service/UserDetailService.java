package com.swproject.hereforus.service;

import com.swproject.hereforus.domain.User;
import com.swproject.hereforus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalAccessException((email)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
