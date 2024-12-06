package com.swproject.hereforus.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.dto.event.PerformanceDto;
import com.swproject.hereforus.entity.event.Performance;
import com.swproject.hereforus.repository.BookmarkRepository;
import com.swproject.hereforus.repository.event.FestivalRepository;
import com.swproject.hereforus.repository.event.FoodRepository;
import com.swproject.hereforus.repository.event.MovieRepository;
import com.swproject.hereforus.repository.event.PerformanceRepository;
import com.swproject.hereforus.service.BookmarkService;
import com.swproject.hereforus.service.GroupService;
import com.swproject.hereforus.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Service
public class PerformanceService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final EnvConfig envConfig;
    private final PerformanceRepository performanceRepository;
    private final BookmarkService bookmarkService;

    /**
     * 공연 상세 정보 조회
     */
    public JsonNode fetchPerformanceInfo(String performanceId) throws JsonProcessingException {

        UriComponents url = UriComponentsBuilder
                .fromUriString(envConfig.getKopisPerformanceBaseUrl())
                .path("/{performanceId}")
                .queryParam("service", envConfig.getKopisServiceKey())
                .encode()
                .buildAndExpand(performanceId);

        XmlMapper xmlMapper = new XmlMapper();

        String response = restTemplate.getForObject(url.toUri(), String.class);
        JsonNode rootNode = xmlMapper.readTree(response);
        JsonNode items = rootNode.path("db");

        return items;
    }

    /** 공연 데이터 호출 및 업데이트 */
    @Scheduled(cron = "0 20 0 * * ?")
    public void fetchPerformances() {
        try {
            int pageNum = 1;

            List<PerformanceDto> performanceList = new ArrayList<>();
            boolean hasMoreData = true;

            while (hasMoreData) {
                UriComponents url = UriComponentsBuilder
                        .fromUriString(envConfig.getKopisPerformanceBaseUrl())
                        .queryParam("service", envConfig.getKopisServiceKey())
                        .queryParam("signgucode", 11)
                        .queryParam("rows", 100)
                        .queryParam("cpage", pageNum)
                        .encode()
                        .build();

                XmlMapper xmlMapper = new XmlMapper();

                String response = restTemplate.getForObject(url.toUri(), String.class);
                JsonNode rootNode = xmlMapper.readTree(response);
                JsonNode items = rootNode.path("db");

                if (items.isEmpty()) {
                    hasMoreData = false;
                } else {
                    for (JsonNode item : items) {
                        PerformanceDto performance = objectMapper.readValue(item.toString(), PerformanceDto.class);

                        JsonNode performanceInfo = fetchPerformanceInfo(performance.getId());
                        // registerLink
                        JsonNode relatesNode = performanceInfo.path("relates").path("relate");
                        if (relatesNode.isArray() && relatesNode.size() > 0) {
                            String registerLink = relatesNode.get(0).path("relateurl").asText();
                            performance.setRegisterLink(registerLink);
                        } else if (relatesNode.isObject()) {
                            String registerLink = relatesNode.path("relateurl").asText();
                            performance.setRegisterLink(registerLink);
                        }

                        // useFee
                        String useFee = performanceInfo.path("pcseguidance").asText();
                        performance.setUseFee(useFee);

                        // useAge
                        String useAge = performanceInfo.path("prfage").asText();
                        performance.setUseAge(useAge);

                        // useTime
                        String useTime = performanceInfo.path("prfruntime").asText();
                        performance.setUseTime(useTime);

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

    public void saveOrUpdatePerformances(PerformanceDto performanceInfo) {
        Optional<Performance> optionalPerformance = performanceRepository.findById(performanceInfo.getId());
        if (optionalPerformance.isPresent()) {
            Performance performance = optionalPerformance.get();
            modelMapper.map(performanceInfo, performance);
            performanceRepository.save(performance);
        } else {
            Performance performance = modelMapper.map(performanceInfo, Performance.class);
            performanceRepository.save(performance);
        }
    }

    public Page<Performance> getPerformanceByDate(String date, Pageable pageable) {
        Page<Performance> performances = performanceRepository.findPerformancesByDate(date, pageable);

        // isBookmarked 값을 엔티티에 설정
        performances.forEach(performance -> {
            boolean isBookmarked = bookmarkService.isBookmarked("performance", performance.getId());
            performance.setBookmarked(isBookmarked);
            performance.setType("performance");
        });
        return performances;
    }

}
