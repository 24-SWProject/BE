package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.FestivalDto;
import com.swproject.hereforus.dto.PerformanceDto;
import com.swproject.hereforus.entity.Festival;
import com.swproject.hereforus.service.EventService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/festival")
    public List<Festival> getFestival(HttpServletRequest request) throws Exception {
        // 쿼리 파라미터로 요청 날짜 받기
        String date = request.getParameter("date");
        // 요청 날짜에 진행 중인 축제 필터링
        List<Festival> ongoingFestivals = eventService.getFestivalsByDate(date);

        return ongoingFestivals;
    }

//    @GetMapping("/performance")
//    public List<PerformanceDto> getPerformance(HttpServletRequest request) throws Exception {
//
//        String date = request.getParameter("date");
//        // 요청 날짜에 진행 중인 공연 필터링
//        List<PerformanceDto> ongoingPerformances = eventService.fetchPerformance(date);
//
//        return ongoingPerformances;
//    }

}