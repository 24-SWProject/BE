package com.swproject.hereforus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Table(name="user_groups")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Group {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToOne
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @Builder
    public Group(User owner) {
        this.owner = owner;
    }
}
