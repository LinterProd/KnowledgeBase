package com.metarash.backend.model.dto.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public ErrorResponse(HttpServletResponse response, String message, HttpServletRequest request) {
        this(LocalDateTime.now(), response.getStatus(), HttpStatus.valueOf(response.getStatus()).getReasonPhrase(), message, request.getRequestURI());
    }
}