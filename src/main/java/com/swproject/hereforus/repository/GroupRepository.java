package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
    @Query("SELECT g FROM Group g WHERE g.inviter.id = :userId AND g.deletedAt IS NULL")
    Optional<Group> findByInviter(@Param("userId") Long userId);

    @Query("SELECT g FROM Group g WHERE g.invitee.id = :userId AND g.deletedAt IS NULL")
    Optional<Group> findByInvitee(@Param("userId") Long userId);

    @Query("SELECT g FROM Group g WHERE g.id = :code AND g.deletedAt IS NULL")
    Optional<Group> findById(@Param("code") String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM Group g WHERE g.inviter.id = :inviterId")
    void deleteByInviter(@Param("inviterId") Long inviterId);
}
