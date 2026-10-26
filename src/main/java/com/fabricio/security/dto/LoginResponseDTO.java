package com.fabricio.security.dto;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken
){}