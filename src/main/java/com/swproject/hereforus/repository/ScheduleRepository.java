package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.Schedule;
import io.swagger.v3.oas.annotations.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByGroup(Group group);

    @Query("SELECT f FROM Schedule f WHERE f.group = :group AND f.scheduleDate <= :endDate AND f.scheduleDate >= :startDate ORDER BY f.scheduleDate ASC")
    List<Schedule> findAllByGroupAndDateRange(@Param("group") Group group,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Modifying
    @Query("DELETE FROM Schedule f WHERE f.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") String groupId);
}
