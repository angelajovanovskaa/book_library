package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequestDTO(
        @NotNull
        @Email(regexp = "\\w+\\.\\w+@(kinandcarta|valtech)\\.com")
        String userEmail,
        @NotBlank
        String userPassword
) {
}
