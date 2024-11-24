package com.swproject.hereforus.entity.event;

import jakarta.persistence.*;
import lombok.*;

@Table(name="performance")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

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
