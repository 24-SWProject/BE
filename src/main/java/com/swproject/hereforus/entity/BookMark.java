package com.swproject.hereforus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name="bookmark")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    private String type; // "festival", "performance", "food"

    @Column(nullable = false, name = "reference_id")
    private Long referenceId;

    @Builder
    public Bookmark(Group group, String type, Long ReferenceId) {
        this.group = group;
        this.type = type;
        this.referenceId = ReferenceId;
    }
}
