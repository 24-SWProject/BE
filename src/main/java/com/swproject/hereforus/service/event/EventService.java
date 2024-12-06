package com.swproject.hereforus.service.event;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.repository.event.FestivalRepository;
import com.swproject.hereforus.repository.event.PerformanceRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Configuration
@RequiredArgsConstructor
@Service
public class EventService {
    private final FestivalRepository festivalRepository;
    private final PerformanceRepository performanceRepository;


    // 이벤트 쿼리 파라미터 확인
    public String checkEventParameter(String date, Integer page, Integer size) {
        if (date == null || page == null || size == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "필수 매개변수를 모두 제공해야 합니다.");
        }
        return null;
    }

    public Object SelectEventById(String type, String id) {
        switch (type) {
            case "festival":
                return festivalRepository.findById(id).orElse(null);
            case "performance":
                return performanceRepository.findById(id).orElse(null);
            default:
                return null;
        }
    }

    public Page<?> SelectEventByTitle(String type, String title, Pageable pageable) {
        String today = String.valueOf(LocalDate.now());

        switch (type) {
            case "festival":
                return festivalRepository.findByTitleContaining(title, today, pageable);
            case "performance":
                return performanceRepository.findByTitleContaining(title, today, pageable);
            default:
                return null;
        }
    }
}
