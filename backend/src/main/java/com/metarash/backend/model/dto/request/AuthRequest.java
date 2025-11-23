package com.metarash.backend.model.dto.request;

public record AuthRequest(
        String username,
        String password
) {}
