package com.swproject.hereforus.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swproject.hereforus.config.EnvConfig;
import com.swproject.hereforus.dto.event.MovieDto;
import com.swproject.hereforus.entity.Movie;
import com.swproject.hereforus.repository.BookmarkRepository;
import com.swproject.hereforus.repository.event.FestivalRepository;
import com.swproject.hereforus.repository.event.FoodRepository;
import com.swproject.hereforus.repository.event.MovieRepository;
import com.swproject.hereforus.repository.event.PerformanceRepository;
import com.swproject.hereforus.service.GroupService;
import com.swproject.hereforus.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Service
public class MovieService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final EnvConfig envConfig;
    private final FestivalRepository festivalRepository;
    private final PerformanceRepository performanceRepository;
    private final FoodRepository foodRepository;
    private final UserDetailService userDetailService;
    private final GroupService groupService;
    private final BookmarkRepository bookmarkRepository;
    private final MovieRepository movieRepository;


    /**
     * 영화 데이터 호출 및 업데이트
     */
    public JsonNode fetchMovieInfo(String title, String openDate) throws JsonProcessingException {

        UriComponents url = UriComponentsBuilder
                .fromUriString(envConfig.getKMDBBaseUrl())
                .queryParam("collection", "kmdb_new2")
                .queryParam("ServiceKey", envConfig.getKMDBServiceKey())
                .queryParam("releaseDts", openDate)
                .queryParam("title", title)
                .encode().build();

        System.out.println(url);

        String response = restTemplate.getForObject(url.toUri(), String.class);
        System.out.println(response);

        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode items = rootNode.path("Data").get(0).path("Result");

        return items;
    }

    @Scheduled(cron = "0 15 0 * * ?")
    public Object fetchBoxOfficeMovies() throws JsonProcessingException {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = LocalDate.now().minusDays(1).format(outputFormatter);

        String url = UriComponentsBuilder
                .fromUriString(envConfig.getKobisBaseUrl())
                .queryParam("key", envConfig.getKobisServiceKey())
                .queryParam("targetDt", formattedDate)
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);

        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode items = rootNode.path("boxOfficeResult").path("dailyBoxOfficeList");

        // movieDto를 담을 list
        List<MovieDto> movies = new ArrayList<>();

        for (JsonNode item : items) {
            // title
            String title = item.path("movieNm").asText();

            // opendate
            LocalDate openDateRaw = LocalDate.parse(item.path("openDt").asText(), inputFormatter);
            String openDate = openDateRaw.format(outputFormatter);

            // audiAcc
            Integer audiAcc = item.path("audiAcc").asInt();

            // poster
            JsonNode movieInfo = fetchMovieInfo(title, openDate);
            String postersRaw = movieInfo.get(0).path("posters").asText();
            String poster = postersRaw.split("\\|")[0];

            // runtime, genre
            Integer runtime = movieInfo.get(0).path("runtime").asInt();
            String genre = movieInfo.get(0).path("genre").asText();

            // actors
            List<String> actorNames = new ArrayList<>();
            JsonNode actors = movieInfo.get(0).path("actors").path("actor");
            for (JsonNode actor : actors) {
                actorNames.add(actor.path("actorNm").asText());
            }

            MovieDto movieDto = new MovieDto(title, openDate, audiAcc, poster, runtime, genre, actorNames);

            System.out.println(movieDto);

            Movie movieEntity = Movie.builder()
                    .title(movieDto.getTitle())
                    .openDate(LocalDate.parse(movieDto.getOpenDate(), outputFormatter))
                    .audiAcc(movieDto.getAudiAcc())
                    .poster(movieDto.getPoster())
                    .genre(movieDto.getGenre())
                    .runtime(movieDto.getRuntime())
                    .actors(String.join(", ", movieDto.getActors()))
                    .build();

            movieRepository.save(movieEntity);

            movies.add(movieDto);
        }

        return movies;
    }

    public List<Movie> getMovieByDate() {
        LocalDate today = LocalDate.now();
        List<Movie> movies = movieRepository.findAllByDate(today);
        return movies;
    }
}
