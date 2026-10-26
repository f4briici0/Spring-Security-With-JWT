package com.fabricio.security.dto;

import com.fabricio.security.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeUserRoleDTO(
        @NotBlank(message = "O campo username não pode estar vazio.")
        String username,
        @NotNull(message = "O campo newRole não pode ser nulo.")
        User.UserRole newRole
)
{}