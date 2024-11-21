package com.swproject.hereforus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.FestivalDto;
import com.swproject.hereforus.dto.PerformanceDto;
import com.swproject.hereforus.entity.Festival;
import com.swproject.hereforus.entity.Performance;
import com.swproject.hereforus.repository.FestivalRepository;
import com.swproject.hereforus.repository.PerformanceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
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
        }
    }

    /** 공연 데이터 호출 및 업데이트 */
    @Scheduled(cron = "0 0 0 * * ?")
    public void fetchPerformances() {
        try {
            int pageNum = 1;

            List<PerformanceDto> performanceList = new ArrayList<>();
            boolean hasMoreData = true;

            while (hasMoreData) {
                String url = UriComponentsBuilder
                        .fromUriString(envConfig.getPerformanceBaseUrl())
                        .queryParam("cpage", pageNum)
                        .toUriString();

                XmlMapper xmlMapper = new XmlMapper();

                String response = restTemplate.getForObject(url, String.class);
                JsonNode rootNode = xmlMapper.readTree(response);
                JsonNode items = rootNode.path("db");

                if (items.isEmpty()) {
                    hasMoreData = false;
                } else {
                    for (JsonNode item : items) {
                        PerformanceDto performance = objectMapper.readValue(item.toString(), PerformanceDto.class);
                        saveOrUpdatePerformances(performance);
                        performanceList.add(performance);
                    }
                    pageNum += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void saveOrUpdatePerformances(PerformanceDto performanceInfo) {
        Optional<Performance> optionalPerformance = performanceRepository.findByTitle(performanceInfo.getTitle());
        if (optionalPerformance.isPresent()) {
            Performance performance = optionalPerformance.get();
            modelMapper.map(performanceInfo, performance);
            performanceRepository.save(performance);
        } else {
            Performance performance = modelMapper.map(performanceInfo, Performance.class);
            performanceRepository.save(performance);
        }
    }

    public Page<Festival> getFestivalsByDate(String date, Pageable pageable) {
        return festivalRepository.findFestivalsByDate(date, pageable);
    }

    public Page<Performance> getPerformanceByDate(String date, Pageable pageable) {
        return performanceRepository.findPerformancesByDate(date, pageable);
    }

    // 이벤트 쿼리 파라미터 확인
    public String checkEventParameter(String date, Integer page, Integer size) {
        if (date == null || page == null || size == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "필수 매개변수를 모두 제공해야 합니다.");
        }
        return null;
    }

}
