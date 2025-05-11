package com.CinephileLog.controller;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.service.MovieSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final MovieSearchService searchService;

    @GetMapping("/search")
    public String searchMovies(@RequestParam("keyword") String keyword, Model model) {
        if (keyword.length() < 2) {
            model.addAttribute("results", List.of());
            model.addAttribute("message", "검색어는 2자 이상 입력하세요.");
            return "search/results";
        }

        List<Movie> results = searchService.search(keyword, 0, 20);
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        return "search/results";
    }
}

