package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
    @Query("SELECT g FROM Group g WHERE g.inviter.id = :userId")
    Optional<Group> findByInviter(@Param("userId") Long userId);

    @Query("SELECT g FROM Group g WHERE g.invitee.id = :userId")
    Optional<Group> findByInvitee(@Param("userId") Long userId);
}
