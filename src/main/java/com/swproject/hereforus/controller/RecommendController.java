package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.FestivalDto;
import com.swproject.hereforus.dto.MovieDto;
import com.swproject.hereforus.dto.WeatherDto;
import com.swproject.hereforus.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/festival")
    public List<FestivalDto> getFestival() throws Exception  {
        return recommendService.fetchFestivals();
    }

    @GetMapping("/movie")
    public List<MovieDto> getMovie() throws Exception {
        return recommendService.fetchMovies();
    }

    @GetMapping("/weather")
    public WeatherDto getWeather() throws Exception {
        return recommendService.fetchTodayWeather();
    }
}