package com.example.blogs.app.api.search.dto;

public enum SearchType {

    RELEVANCE,

    NEWEST,

    OLDEST;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
