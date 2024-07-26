package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateRoleRequestDTO(
        @NotNull
        UUID userId,
        @NotBlank
        String role
) {
}
