package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UserUpdateRoleRequestDTO(
        @NotNull
        UUID userId,
        @Pattern(regexp = "ADMIN|USER", message = "Role must be either ADMIN or USER")
        String role
) {
}
