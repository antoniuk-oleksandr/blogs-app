package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.search.dto.SearchPostsResponseDTO;
import com.example.blogs.app.api.search.dto.SearchType;

public interface SearchService {

    SearchPostsResponseDTO searchPosts(String query, SearchType type, String cursor);
}
