package com.swproject.hereforus.Service;

import com.swproject.hereforus.Dto.FestivalDto;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class RecommendService {

    /** 행사(festival) 데이터 현재 날짜 기점으로 필터링 */
    public List<FestivalDto> filteredFestival(List<FestivalDto> festivalList) {
        LocalDate today = LocalDate.now();  // 오늘 날짜
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        return festivalList.stream()
                .filter(festival -> {
                    LocalDate startDate = LocalDate.parse(festival.getStrtDate(), formatter);
                    LocalDate endDate = LocalDate.parse(festival.getEndDate(), formatter);

                    // 오늘 날짜가 startDate와 endDate 사이에 있는지 확인
                    return (today.isEqual(startDate) || today.isAfter(startDate)) && today.isBefore(endDate);
                })
                .collect(Collectors.toList());
    }
}

