package com.swproject.hereforus.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name="festivals")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Festival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String guName;

    private String place;

    @Column(columnDefinition = "TEXT")
    private String useTrgt;

    @Column(columnDefinition = "TEXT")
    private String useFee;

    @Column(columnDefinition = "TEXT")
    private String registerLink;

    @Column(columnDefinition = "TEXT")
    private String poster;

    private String registerDate;

    private String openDate;

    private String endDate;

    private String gpsX;

    private String gpsY;
}
