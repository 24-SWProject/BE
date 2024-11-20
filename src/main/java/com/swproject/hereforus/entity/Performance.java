package com.swproject.hereforus.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Table(name="performance")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Performance {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "DATE")
    private String openDate;

    @Column(columnDefinition = "DATE")
    private String endDate;

    private String state;

    @Column(columnDefinition = "TEXT")
    private String place;

    @Column(columnDefinition = "TEXT")
    private String poster;

    private String category;
}
