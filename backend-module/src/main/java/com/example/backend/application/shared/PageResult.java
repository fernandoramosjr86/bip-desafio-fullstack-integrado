package com.example.backend.application.shared;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        long totalItems,
        int page,
        int size,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
