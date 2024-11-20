package com.swproject.hereforus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Table(name="user")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    private String profileImg;

    private String birthYear;

    private String birthDate;

    @OneToOne(mappedBy = "inviter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Group group;

    @CreationTimestamp
    @Column(name= "createdAt", nullable = false, updatable = false)
    private LocalDate createdAt;

    @CreationTimestamp
    @Column(name= "updatedAt", nullable = false)
    private LocalDate updatedAt;

    @CreationTimestamp
    @Column(name= "deletedAt")
    private LocalDate deletedAt;

    @Builder
    public User(String email, String nickname, String profileImg, String birthYear, String birthDate) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.birthYear = birthYear;
        this.birthDate = birthDate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return "";
    }


    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    // 패스워드 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
       return true;
    }

    // 계정 사용 가능 여부
    @Override
    public boolean isEnabled(){
        return true;
    }

}
