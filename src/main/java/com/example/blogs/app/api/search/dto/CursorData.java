package com.example.blogs.app.api.search.dto;

import java.util.List;

public record CursorData(
        List<Object> sortValues
) {
}
