package com.swproject.hereforus.repository.event;

import com.swproject.hereforus.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT f FROM Movie f WHERE f.createdAt = :date ORDER BY f.id ASC")
    List<Movie> findAllByDate(@Param("date") LocalDate today);
}