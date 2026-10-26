package com.fabricio.security.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequestDTO(
        @NotBlank(message = "O Refresh Token é obrigatório")
        String refreshToken
) { }