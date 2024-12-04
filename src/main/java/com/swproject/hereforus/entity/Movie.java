package com.swproject.hereforus.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;

@Table(name="movies")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate openDate;

    private Integer audiAcc;

    @Column(columnDefinition = "TEXT")
    private String actors;

    @Column(columnDefinition = "TEXT")
    private String poster;

    private String title;

    private String genre;

    private Integer runtime;

    @CreationTimestamp
    @Column(name= "createdAt", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Builder
    public Movie(String title, Integer runtime, String genre, String poster, String actors, Integer audiAcc, LocalDate openDate ) {
        this.title = title;
        this.runtime = runtime;
        this.genre = genre;
        this.poster = poster;
        this.actors = actors;
        this.audiAcc = audiAcc;
        this.openDate = openDate;
    }
}
