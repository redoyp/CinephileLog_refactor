package com.CinephileLog.external.controller;

import com.CinephileLog.external.service.TmdbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TmdbController {
    private final TmdbService tmdbService;

    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

//    @GetMapping("/sync-genres")
//    public String syncGenres() {
//        tmdbService.saveGenres();
//        return "ok";
//    }

//    @GetMapping("/sync-movies")
//    public String syncMovies() {
//        tmdbService.saveMovies();
//        return "ok";
//    }
}
