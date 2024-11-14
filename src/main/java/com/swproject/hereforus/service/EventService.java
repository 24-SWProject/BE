package com.swproject.hereforus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.config.error.ErrorCode;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.dto.FestivalDto;
import com.swproject.hereforus.dto.MovieDto;
import com.swproject.hereforus.dto.PerformanceDto;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
public class EventService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EnvConfig envConfig;

    /** 서울시 문화 행사 데이터 호출 */
    public List<FestivalDto> fetchFestivals(String requestedDate) throws Exception {
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

                        // 날짜 조건에 맞는 축제만 추가
                        if (isOnGoingByDate(festival.getOpenDate(), festival.getEndDate(), requestedDate)) {
                            festivalList.add(festival);
                        }

                    }
                    start += 1000;
                    end += 1000;
                }
            }

            System.out.println(festivalList);
            return festivalList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** 행사가 현재 날짜 기점으로 진행 중인지 확인 */
    public boolean isOnGoingToday(FestivalDto festival) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        LocalDate startDate = LocalDate.parse(festival.getOpenDate(), formatter);
        LocalDate endDate = LocalDate.parse(festival.getEndDate(), formatter);

        return (date.isEqual(startDate) || date.isAfter(startDate)) && date.isBefore(endDate);
    }

    /** 행사가 사용자가 요청한 날짜 기점으로 진행 중인지 확인 */
    public boolean isOnGoingAtRequestedDate(FestivalDto festival, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        LocalDate requestedDate = LocalDate.parse(date, formatter);
        LocalDate startDate = LocalDate.parse(festival.getOpenDate(), formatter);
        LocalDate endDate = LocalDate.parse(festival.getEndDate(), formatter);

        return (requestedDate.isEqual(startDate) || requestedDate.isAfter(startDate)) && requestedDate.isBefore(endDate);
    }

    /** 일별 박스오피스 영화 데이터 호출*/
    public List<MovieDto> fetchMovies() throws Exception {

        LocalDate date = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = date.format(formatter);

        List<MovieDto> movieList = new ArrayList<>();

        String url = UriComponentsBuilder
                .fromUriString(envConfig.getMovieBaseUrl())
                .queryParam("key", envConfig.getMovieKey())
                .queryParam("targetDt", formattedDate)
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode items = rootNode.path("boxOfficeResult").path("dailyBoxOfficeList");

        for (JsonNode item : items) {
            MovieDto movie = objectMapper.readValue(item.toString(), MovieDto.class);
            movieList.add(movie);
        }

        return movieList;
    }

    /** 현재 진행 중인 공연 데이터 호출 */
    public List<PerformanceDto> fetchPerformance() throws Exception {

        List<PerformanceDto> performanceList = new ArrayList<>();

        String response = restTemplate.getForObject(envConfig.getPerformanceBaseUrl(), String.class);

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode rootNode = xmlMapper.readTree(response);
        JsonNode items = rootNode.path("db");

        for (JsonNode item : items) {
            PerformanceDto performance = xmlMapper.treeToValue(item, PerformanceDto.class);
            performanceList.add(performance);
        }

        return performanceList;
    }

    // 오늘 상영 영화
    // 날짜 받은 후 진행중인 공연
    // performance url에서 받은 날짜 필터링

    /** 공연이 사용자가 요청한 날짜 기점으로 진행 중인지 확인 */
    public boolean isOnGoingByDate(String openDate, String endDate, String requestedDate) {
        try {
            // 여러 날짜 형식을 처리할 수 있는 Formatter 생성
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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

    // 날짜 받은 후 진행중인 축제
}
