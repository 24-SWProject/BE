package com.swproject.hereforus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.config.error.ErrorCode;
import com.swproject.hereforus.dto.FestivalDto;
import com.swproject.hereforus.dto.PerformanceDto;
import com.swproject.hereforus.dto.UserDto;
import com.swproject.hereforus.entity.Festival;
import com.swproject.hereforus.entity.User;
import com.swproject.hereforus.repository.FestivalRepository;
import com.swproject.hereforus.repository.PerformanceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Service
public class EventService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private PerformanceRepository performanceRepository;


    /** 축제 데이터 호출 및 업데이트 */
    @Scheduled(cron = "0 0 0 * * ?")
    public void fetchFestivals() {
        try {
            int start = 1;
            int end = 1000;

            List<FestivalDto> festivalList = new ArrayList<>();
            boolean hasMoreData = true;

            while (hasMoreData) {
                String url = UriComponentsBuilder
                        .fromUriString(envConfig.getFestivalBaseUrl())
                        .buildAndExpand(start, end)
                        .toUriString();

                String response = restTemplate.getForObject(url, String.class);
                JsonNode rootNode = objectMapper.readTree(response);
                JsonNode items = rootNode.path("culturalEventInfo").path("row");

                if (items.isEmpty()) {
                    hasMoreData = false;
                } else {
                    for (JsonNode item : items) {
                        FestivalDto festival = objectMapper.readValue(item.toString(), FestivalDto.class);
                        saveOrUpdateFestival(festival);
                        festivalList.add(festival);
                    }
                    start += 1000;
                    end += 1000;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** 공연 데이터 호출 */
/*
    public List<PerformanceDto> fetchPerformance(String requestedDate) {
        try {
            int pageNum = 1;

            List<PerformanceDto> performanceList = new ArrayList<>();
            boolean hasMoreData = true;

            while (hasMoreData) {
                String url = UriComponentsBuilder
                        .fromUriString(envConfig.getPerformanceBaseUrl())
                        .queryParam("cpage", pageNum)
                        .toUriString();
                System.out.println(url);

                XmlMapper xmlMapper = new XmlMapper();

                String response = restTemplate.getForObject(url, String.class);
                JsonNode rootNode = xmlMapper.readTree(response);
                JsonNode items = rootNode.path("db");
                System.out.println(items);

                if (items.isEmpty()) {
                    hasMoreData = false;
                } else {
                    for (JsonNode item : items) {
                        PerformanceDto performance = objectMapper.readValue(item.toString(), PerformanceDto.class);

                        // 날짜 조건에 맞는 공연만 추가
                        if (isOnGoingByDate(performance.getOpenDate(), performance.getEndDate(), requestedDate)) {
                            performanceList.add(performance);
                        }

                    }
                    pageNum += 1;
                }
            }
            System.out.println(performanceList);
            return performanceList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
*/

    /** 공연이 사용자가 요청한 날짜 기점으로 진행 중인지 확인 */
    public boolean isOnGoingByDate(String openDate, String endDate, String requestedDate) {
        try {
            // 여러 날짜 형식을 처리할 수 있는 Formatter 생성
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .toFormatter();

            LocalDate requestedDateToLocal = LocalDate.parse(requestedDate, formatter);
            LocalDate openDateToLocal = LocalDate.parse(openDate, formatter);
            LocalDate endDateToLocale = LocalDate.parse(endDate, formatter);

            return (requestedDateToLocal.isEqual(openDateToLocal) || requestedDateToLocal.isEqual(endDateToLocale) || requestedDateToLocal.isAfter(openDateToLocal)) && requestedDateToLocal.isBefore(endDateToLocale);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    public void saveOrUpdateFestival(FestivalDto festivalInfo) {
        Optional<Festival> optionalFestival  = festivalRepository.findByTitle(festivalInfo.getTitle());
        if (optionalFestival.isPresent()) {
            Festival festival = optionalFestival.get();
            modelMapper.map(festivalInfo, festival);
            festivalRepository.save(festival);
        } else {
            Festival festival = modelMapper.map(festivalInfo, Festival.class);
            festivalRepository.save(festival);
        }
    }

    public List<Festival> getFestivalsByDate(String requestedDate) {
        // 여러 날짜 형식을 처리할 수 있는 Formatter 생성
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
                .toFormatter();

        // 요청된 날짜를 LocalDate로 변환
        LocalDate date = LocalDate.parse(requestedDate, formatter);

        // 특정 날짜에 해당하는 축제 목록 조회
        return festivalRepository.findFestivalsByDate(date);
    }
}
