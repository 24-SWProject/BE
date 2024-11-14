package com.swproject.hereforus.repository;

import com.swproject.hereforus.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    Optional<Performance> findByTitle(String title);
}
