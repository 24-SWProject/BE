package com.swproject.hereforus.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name="festival")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Festival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
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

    @Column(columnDefinition = "DATE")
    private String registerDate;

    @Column(columnDefinition = "DATE")
    private String openDate;

    @Column(columnDefinition = "DATE")
    private String endDate;

    private String gpsX;

    private String gpsY;
}
