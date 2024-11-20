package com.swproject.hereforus.controller;

import com.swproject.hereforus.entity.Food;
import com.swproject.hereforus.service.FoodService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/food")
public class FoodController {
    private final FoodService foodService;

    @Hidden
    @GetMapping
    public Page<Food> getPagedFoodData(Pageable pageable) {
        return foodService.getPagedFoodData(pageable);
    }
}