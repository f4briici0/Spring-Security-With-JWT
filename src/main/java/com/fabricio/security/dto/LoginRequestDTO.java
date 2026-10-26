package com.fabricio.security.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (
        @NotBlank(message = "O campo username não pode estar vazio.")
        String username,
        @NotBlank(message = "O campo password não pode estar vazio.")
        String password
){}
