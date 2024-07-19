package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO(
        @NotBlank
        String userEmail,
        @NotBlank
        String userPassword
) {
}
