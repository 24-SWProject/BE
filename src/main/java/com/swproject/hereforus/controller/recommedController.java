package com.swproject.hereforus.controller;

import com.swproject.hereforus.service.RecommendService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Recommend", description = "데이트 코스 추천 REST API에 대한 명세를 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/recommend")
public class recommedController {
    private final RecommendService recommendService;

    @Hidden
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getRecommend(
            @RequestBody String keyword
            ) {
        return recommendService.fetchRecommendByAI(keyword);
    }
}
