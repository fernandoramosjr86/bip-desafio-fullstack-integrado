package com.example.backend.adapters.in.web.dto;

import java.util.Map;

public record ValidationErrorResponse(
        String message,
        Map<String, String> fields
) {
}
