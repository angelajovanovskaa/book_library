package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserChangePasswordRequestDTO(
        @NotBlank
        UUID userId,
        @NotBlank
        String oldPassword,
        @NotBlank
        String newPassword
) {
}
