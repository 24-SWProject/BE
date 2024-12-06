package com.swproject.hereforus.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.dto.event.FestivalDto;
import com.swproject.hereforus.dto.event.PerformanceDto;
import com.swproject.hereforus.entity.event.Festival;
import com.swproject.hereforus.entity.event.Performance;
import com.swproject.hereforus.repository.event.FestivalRepository;
import com.swproject.hereforus.repository.event.PerformanceRepository;
import com.swproject.hereforus.service.BookmarkService;
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
public class FestivalService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final EnvConfig envConfig;
    private final PerformanceRepository performanceRepository;
    private final BookmarkService bookmarkService;
    private final FestivalRepository festivalRepository;

    /**
     * 축제 상세 정보 조회
     */
    public JsonNode fetchFestivalInfo(String festivalId) throws JsonProcessingException {

        UriComponents url = UriComponentsBuilder
                .fromUriString(envConfig.getKopisFestivalBaseUrl())
                .path("/{festivalId}")
                .queryParam("service", envConfig.getKopisServiceKey())
                .encode()
                .buildAndExpand(festivalId);

        XmlMapper xmlMapper = new XmlMapper();

        String response = restTemplate.getForObject(url.toUri(), String.class);
        JsonNode rootNode = xmlMapper.readTree(response);
        JsonNode items = rootNode.path("db");

        return items;
    }

    /** 축제 데이터 호출 및 업데이트 */
    @Scheduled(cron = "0 10 0 * * ?")
    public void fetchFestivals() {
        try {
            int pageNum = 1;

            List<FestivalDto> festivalDtoList = new ArrayList<>();
            boolean hasMoreData = true;

            while (hasMoreData) {
                UriComponents url = UriComponentsBuilder
                        .fromUriString(envConfig.getKopisFestivalBaseUrl())
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
                        FestivalDto festival = objectMapper.readValue(item.toString(), FestivalDto.class);

                        JsonNode festivalInfo = fetchFestivalInfo(festival.getId());
                        // registerLink
                        JsonNode relatesNode = festivalInfo.path("relates").path("relate");
                        if (relatesNode.isArray() && relatesNode.size() > 0) {
                            String registerLink = relatesNode.get(0).path("relateurl").asText();
                            festival.setRegisterLink(registerLink);
                        } else if (relatesNode.isObject()) {
                            String registerLink = relatesNode.path("relateurl").asText();
                            festival.setRegisterLink(registerLink);
                        }

                        // useFee
                        String useFee = festivalInfo.path("pcseguidance").asText();
                        festival.setUseFee(useFee);

                        // useAge
                        String useAge = festivalInfo.path("prfage").asText();
                        festival.setUseAge(useAge);

                        // useTime
                        String useTime = festivalInfo.path("prfruntime").asText();
                        festival.setUseTime(useTime);

                        saveOrUpdateFestivals(festival);
                        festivalDtoList.add(festival);
                    }
                    pageNum += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveOrUpdateFestivals(FestivalDto festivalInfo) {
        Optional<Festival> optionalFestival = festivalRepository.findById(festivalInfo.getId());
        if (optionalFestival.isPresent()) {
            Festival festival = optionalFestival.get();
            modelMapper.map(festivalInfo, festival);
            festivalRepository.save(festival);
        } else {
            Festival festival = modelMapper.map(festivalInfo, Festival.class);
            festivalRepository.save(festival);
        }
    }

    public Page<Festival> getFestivalByDate(String date, Pageable pageable) {
        Page<Festival> festivals = festivalRepository.findFestivalsByDate(date, pageable);

        // isBookmarked 값을 엔티티에 설정
        festivals.forEach(festival -> {
            boolean isBookmarked = bookmarkService.isBookmarked("festival", festival.getId());
            festival.setBookmarked(isBookmarked);
            festival.setType("performance");
        });
        return festivals;
    }

}
