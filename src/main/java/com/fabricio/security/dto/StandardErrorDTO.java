package com.fabricio.security.dto;

import java.time.Instant;

public record StandardErrorDTO(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}