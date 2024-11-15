package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Festival;
import com.swproject.hereforus.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    Optional<Performance> findByTitle(String title);

    @Query("SELECT f FROM Performance f WHERE f.openDate <= :date AND f.endDate >= :date")
    List<Performance> findPerformancesByDate(@Param("date") String date);
}
