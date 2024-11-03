package com.swproject.hereforus.Controller;

import com.swproject.hereforus.Dto.FestivalDto;
import com.swproject.hereforus.Dto.MovieDto;
import com.swproject.hereforus.Dto.WeatherDto;
import com.swproject.hereforus.Service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/festival")
    public List<FestivalDto> getFestival() throws Exception  {
        String baseUrl = "http://openapi.seoul.go.kr:8088/{serviceKey}/{type}/{serviceName}/{start}/{end}";
        String serviceKey = "6c595370636e696e3130396f446f476c";
        String type = "json";
        String serviceName = "culturalEventInfo";

        int start = 1;
        int end = 1000;

        List<FestivalDto> festivalDtoList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        Boolean hasMoreData = true;
        ObjectMapper objectMapper = new ObjectMapper();

        while (hasMoreData) {
            String url = UriComponentsBuilder
                    .fromUriString(baseUrl)
                    .buildAndExpand(serviceKey, type, serviceName, start, end)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("culturalEventInfo").path("row");

            if (items.isEmpty()) {
                hasMoreData = false;
            } else {
                for (JsonNode item : items) {
                    FestivalDto festivalDto = objectMapper.readValue(item.toString(), FestivalDto.class);
                    festivalDtoList.add(festivalDto);
                }

                // 다음 페이지로 이동 (start와 end 값을 증가)
                start += 1000;
                end += 1000;
            }
        }

        return recommendService.filteredFestival(festivalDtoList);
    }

    @GetMapping("/movie")
    public List<MovieDto> getMovie() throws Exception {
        // 현재 날짜에서 하루를 뺍니다.
        LocalDate date = LocalDate.now().minusDays(1);
        // 날짜를 원하는 형식으로 포맷팅합니다 (예: "yyyyMMdd").
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = date.format(formatter);

        String serviceKey = "7832fc0259303b0c8ea2c9e2ca294758";
        String baseUrl = String.format("http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=%s&targetDt=%s", serviceKey, formattedDate);

        List<MovieDto> movieDtoList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .buildAndExpand(serviceKey)
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);

        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode items = rootNode.path("boxOfficeResult").path("dailyBoxOfficeList");

        for (JsonNode item : items) {
            MovieDto movieDto = objectMapper.readValue(item.toString(), MovieDto.class);
            movieDtoList.add(movieDto);
        }
        return movieDtoList;
    }

    @GetMapping("/weather")
    public WeatherDto getWeather() throws Exception {
        String serviceKey = "83d62e07628b4874587cbb35537b9fa7";
        String baseUrl = String.format("https://api.openweathermap.org/data/2.5/weather?id=1835841&appid=%s&units=metric", serviceKey);

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // API 호출하여 응답을 JSON 형식으로 가져옵니다.
        String response = restTemplate.getForObject(baseUrl, String.class);

        // JSON 응답 처리
        JsonNode rootNode = objectMapper.readTree(response);

        // WeatherDto 객체 생성
        WeatherDto weatherDto = new WeatherDto();

        // weather 정보 추출
        JsonNode weatherNode = rootNode.path("weather").get(0);
        weatherDto.setMain(weatherNode.path("main").asText());
        weatherDto.setDescription(weatherNode.path("description").asText());
        weatherDto.setIcon(weatherNode.path("icon").asText());

        // main 정보 추출
        JsonNode mainNode = rootNode.path("main");
        weatherDto.setTemperature(mainNode.path("temp").asDouble());
        weatherDto.setFeelsLike(mainNode.path("feels_like").asDouble());
        weatherDto.setTempMin(mainNode.path("temp_min").asDouble());
        weatherDto.setTempMax(mainNode.path("temp_max").asDouble());
        weatherDto.setPressure(mainNode.path("pressure").asInt());
        weatherDto.setHumidity(mainNode.path("humidity").asInt());

        return weatherDto; 
    }
}