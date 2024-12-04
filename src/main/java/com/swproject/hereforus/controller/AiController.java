package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.entity.event.Festival;
import com.swproject.hereforus.entity.event.Food;
import com.swproject.hereforus.entity.event.Performance;
import com.swproject.hereforus.repository.event.FestivalRepository;
import com.swproject.hereforus.repository.event.FoodRepository;
import com.swproject.hereforus.repository.event.PerformanceRepository;
import com.swproject.hereforus.service.EventService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final EventService eventService;
    private final FoodRepository foodRepository;
    private final FestivalRepository festivalRepository;
    private final PerformanceRepository performanceRepository;

    @Hidden
    @GetMapping("/festival")
    public ResponseEntity<?> getFestival(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        try {
            eventService.checkEventParameter(date, page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<Festival> festivals = festivalRepository.findFestivalsByDate(date, pageable);
            return ResponseEntity.ok(festivals);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Hidden
    @GetMapping("/performance")
    public ResponseEntity<?> getPerformance(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        try {
            eventService.checkEventParameter(date, page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<Performance> performances = performanceRepository.findPerformancesByDate(date, pageable);
            return ResponseEntity.ok(performances);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Hidden
    @GetMapping("/food")
    public ResponseEntity<?> getFood(Pageable pageable) {
        try {
            Page<Food> foods = foodRepository.findAll(pageable);
            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
