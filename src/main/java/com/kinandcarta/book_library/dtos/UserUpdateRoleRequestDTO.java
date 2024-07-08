package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserUpdateRoleRequestDTO(
        @NotBlank
        UUID userId,
        @NotBlank
        String role
) {
}
