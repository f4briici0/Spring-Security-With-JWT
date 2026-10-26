package com.fabricio.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "O campo username não pode estar vazio.")
        @Size(min = 3, max = 50, message = "O username deve ter entre 3 e 50 caracteres.")
        String username,

        @NotBlank(message = "O campo password não pode estar vazio.")
        @Size(min = 6, max = 64, message = "A senha deve ter entre 6 e 64 caracteres.")
        String password
){}