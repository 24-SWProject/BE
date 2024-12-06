package com.swproject.hereforus.repository.event;

import com.swproject.hereforus.entity.event.Festival;
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
public interface FestivalRepository extends JpaRepository<Festival, String> {
    Optional<Festival> findByTitle(String title);

    @Query("SELECT f FROM Festival f WHERE f.openDate <= :date AND f.endDate >= :date ORDER BY f.endDate ASC")
    Page<Festival> findFestivalsByDate(@Param("date") String date, Pageable pageable);

    @Query("SELECT f FROM Festival f WHERE f.title LIKE %:title% AND f.endDate >= :today ORDER BY f.endDate ASC")
    Page<Festival> findByTitleContaining(@Param("title") String title, @Param("today") String today, Pageable pageable);
}
