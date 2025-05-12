package com.CinephileLog.column.controller;

import com.CinephileLog.column.dto.ColumnArticleRequest;
import com.CinephileLog.column.dto.ColumnArticleResponse;
import com.CinephileLog.column.service.ColumnArticleService;
import com.CinephileLog.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/column")
public class ColumnArticleController {

    private final ColumnArticleService columnArticleService;
    private final MovieRepository movieRepository;

    @GetMapping
    public String columnList(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "createdDate") String sort,
                             @RequestParam(defaultValue = "desc") String direction,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String field,
                             Model model) {

        Page<ColumnArticleResponse> columns = columnArticleService.getColumnPage(page, size, sort, direction, keyword, field);
        int blockSize = 10;
        int currentPage = columns.getNumber() + 1;
        int totalPages = columns.getTotalPages();
        int pageBlockStart = ((currentPage - 1) / blockSize) * blockSize + 1;
        int pageBlockEnd = Math.min(pageBlockStart + blockSize - 1, totalPages);
        model.addAttribute("columns", columns);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("keyword", keyword);
        model.addAttribute("field", field);
        model.addAttribute("pageBlockStart", pageBlockStart);
        model.addAttribute("pageBlockEnd", pageBlockEnd);
        return "column/list";
    }

    @GetMapping("/view/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("column", columnArticleService.getColumnDetail(id));
        return "column/detail";
    }

    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("column", new ColumnArticleRequest());
        return "column/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        ColumnArticleResponse column = columnArticleService.getColumnDetail(id);
        model.addAttribute("column", column);
        return "column/form";
    }

    @PostMapping("/save")
    public String save(@AuthenticationPrincipal OAuth2User oAuth2User,
                       @ModelAttribute ColumnArticleRequest request) throws AccessDeniedException {
        Long userId = oAuth2User.getAttribute("userId");
        if (request.getColumnId() == null) {
            columnArticleService.writeColumn(userId, request);
        } else {
            columnArticleService.updateColumn(userId, request.getColumnId(), request);
        }
        return "redirect:/column";
    }

    @PostMapping("/delete/{id}")
    public String delete(@AuthenticationPrincipal OAuth2User oAuth2User, @PathVariable Long id) throws AccessDeniedException {
        Long userId = oAuth2User.getAttribute("userId");
        columnArticleService.deleteColumn(userId, id);
        return "redirect:/column";
    }
}
