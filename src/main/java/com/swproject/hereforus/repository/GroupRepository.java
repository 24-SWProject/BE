package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g WHERE g.inviter.id = :userId")
    Optional<Group> findByUserId(@Param("userId") Long userId);
}
