package com.undoschool.course_search.controller;

import com.undoschool.course_search.document.CourseDocument;
import com.undoschool.course_search.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final CourseSearchService service;

    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        SearchHits<CourseDocument> hits =
                service.search(q, minAge, maxAge, category, type,
                        minPrice, maxPrice, startDate, sort, page, size);

        return Map.of(
                "total", hits.getTotalHits(),
                "courses", hits.getSearchHits()
                        .stream()
                        .map(hit -> hit.getContent())
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/search/suggest")
    public List<String> suggest(@RequestParam String q) throws Exception {
        return service.suggest(q);
    }
}