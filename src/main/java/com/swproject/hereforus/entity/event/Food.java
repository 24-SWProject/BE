package com.swproject.hereforus.entity.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private String title;
    private String phoneNumber;
    private String guName;
    private String address;
    private Double gpsX;
    private Double gpsY;
    private String majorCategory;
    private String subCategory;
}