package com.swproject.hereforus.repository.event;

import com.swproject.hereforus.entity.event.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, String> {
    Optional<Performance> findByTitle(String title);

    @Query("SELECT f FROM Performance f WHERE f.openDate <= :date AND f.endDate >= :date ORDER BY f.endDate ASC")
    Page<Performance> findPerformancesByDate(@Param("date") String date, Pageable pageable);

    @Query("SELECT p FROM Performance p WHERE p.title LIKE %:title% AND p.endDate >= :today ORDER BY p.endDate ASC")
    Page<Performance> findByTitleContaining(@Param("title") String title, @Param("today") String today, Pageable pageable);
}
