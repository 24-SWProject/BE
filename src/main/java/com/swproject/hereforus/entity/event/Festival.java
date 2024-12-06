package com.swproject.hereforus.entity.event;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Table(name="festival")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Festival {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column
    private String category;

    @Column(length = 512)
    private String place;

    @Column(columnDefinition = "DATE")
    private String openDate;

    @Column(columnDefinition = "DATE")
    private String endDate;

    @Column(length = 2048)
    private String poster;

    @Column
    private String state;

    @Column(length = 2048)
    private String registerLink;

    @Column(length = 512)
    private String useFee;

    @Column
    private String useAge;

    @Column
    private String useTime;

    @Transient
    private boolean bookmarked;

    @Transient
    private String type;

    @CreationTimestamp
    @Column(name= "createdAt", nullable = false, updatable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name= "updatedAt", nullable = false)
    private LocalDate updatedAt;
}

