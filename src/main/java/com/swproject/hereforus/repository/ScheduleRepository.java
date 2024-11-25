package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Group;
import com.swproject.hereforus.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByGroup(Group group);
}
