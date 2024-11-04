package com.swproject.hereforus.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swproject.hereforus.Dto.FestivalDto;
import com.swproject.hereforus.Dto.MovieDto;
import com.swproject.hereforus.Dto.WeatherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@PropertySource("classpath:env.properties")
@Service
public class RecommendService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${FESTIVAL_BASEURL}")
    private String festivalBaseUrl;

    @Value("${MOVIE_BASEURL}")
    private String movieBaseUrl;

    @Value("${MOVIE_KEY}")
    private String movieKey;

    @Value("${WEATHER_BASEURL}")
    private String weatherBaseUrl;

    /** 서울시 문화 행사 데이터 호출 */
    public List<FestivalDto> fetchFestivals() throws Exception {
        int start = 1;
        int end = 1000;

        List<FestivalDto> festivalList = new ArrayList<>();
        boolean hasMoreData = true;

        while (hasMoreData) {
            String url = UriComponentsBuilder
                    .fromUriString(festivalBaseUrl)
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
                    if (isOnGoing(festival)) {  // 진행 중인 축제 필터링
                        festivalList.add(festival);
                    }
                }
                start += 1000;
                end += 1000;
            }
        }

        return festivalList;
    }

    /** 행사가 현재 날짜 기점으로 진행 중인지 확인 */
    public boolean isOnGoing(FestivalDto festival) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        LocalDate startDate = LocalDate.parse(festival.getStrtDate(), formatter);
        LocalDate endDate = LocalDate.parse(festival.getEndDate(), formatter);

        return (date.isEqual(startDate) || date.isAfter(startDate)) && date.isBefore(endDate);
    }

    /** 일별 박스오피스 영화 데이터 호출 */
    public List<MovieDto> fetchMovies() throws Exception {

        LocalDate date = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = date.format(formatter);

        List<MovieDto> movieList = new ArrayList<>();

        String url = UriComponentsBuilder
                .fromUriString(movieBaseUrl)
                .queryParam("key", movieKey)
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

    /** 오늘 날씨 데이터 호출 */
    public WeatherDto fetchTodayWeather() throws Exception {

        String response = restTemplate.getForObject(weatherBaseUrl, String.class);
        JsonNode rootNode = objectMapper.readTree(response);

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

