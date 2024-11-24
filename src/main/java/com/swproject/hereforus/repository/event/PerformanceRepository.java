package com.swproject.hereforus.repository.event;

import com.swproject.hereforus.entity.event.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    Optional<Performance> findByTitle(String title);

    @Query("SELECT f FROM Performance f WHERE f.openDate <= :date AND f.endDate >= :date")
    Page<Performance> findPerformancesByDate(@Param("date") String date, Pageable pageable);
}
