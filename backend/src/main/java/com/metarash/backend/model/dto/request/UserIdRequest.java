package com.metarash.backend.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserIdRequest (
        @NotNull Long userId
) {}
