package com.swproject.hereforus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Table(name="user_group")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user_group SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Entity
public class Group {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @OneToOne
    @JoinColumn(name = "invitee_id")
    private User invitee;

    private String nickName;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate anniversary;

    private String profileImg;

    @CreationTimestamp
    @Column(name= "createdAt", nullable = false, updatable = false)
    private LocalDate createdAt;

    @CreationTimestamp
    @Column(name= "updatedAt", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "deletedAt")
    private LocalDate deletedAt = null;

    @Builder
    public Group(User inviter, String nickName, LocalDate anniversary, String profileImg) {
        this.inviter = inviter;
        this.nickName = nickName;
        this.anniversary = anniversary;
        this.profileImg = profileImg;
    }
}
