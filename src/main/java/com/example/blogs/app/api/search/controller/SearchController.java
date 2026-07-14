package com.example.blogs.app.api.search.controller;

import com.example.blogs.app.api.search.docs.SearchControllerDocs;
import com.example.blogs.app.api.search.dto.SearchPostsResponseDTO;
import com.example.blogs.app.api.search.dto.SearchType;
import com.example.blogs.app.api.search.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("")
    @SearchControllerDocs.SearchPosts
    public ResponseEntity<SearchPostsResponseDTO> searchPosts(
            @RequestParam("query") String query,
            @RequestParam(value = "type", required = false, defaultValue = "RELEVANCE") SearchType type,
            @RequestParam(value = "cursor", required = false) String cursor
    ) {
        return ResponseEntity
                .ok(searchService.searchPosts(query, type, cursor));
    }

}
