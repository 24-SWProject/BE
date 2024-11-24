package com.swproject.hereforus.service;

import com.swproject.hereforus.entity.event.Food;
import com.swproject.hereforus.repository.event.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public Page<Food> getPagedFoodData(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }
}
