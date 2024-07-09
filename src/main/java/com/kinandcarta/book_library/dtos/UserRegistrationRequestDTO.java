package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequestDTO(
        @NotBlank
        String fullName,
        @Email(regexp = "\\w+@(kinandcarta|valtech)\\.com")
        String email,
        @NotBlank
        String password

) {
}
