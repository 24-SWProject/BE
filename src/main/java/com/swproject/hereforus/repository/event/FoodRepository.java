package com.swproject.hereforus.repository.event;

import com.swproject.hereforus.entity.event.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
}