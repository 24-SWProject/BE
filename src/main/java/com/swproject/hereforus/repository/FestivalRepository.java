package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
    Optional<Festival> findByTitle(String title);

    @Query("SELECT f FROM Festival f WHERE f.openDate <= :date AND f.endDate >= :date")
    List<Festival> findFestivalsByDate(@Param("date") LocalDate date);
}
