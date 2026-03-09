package com.example.backend.adapters.in.web.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        long totalItems,
        int page,
        int size,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
