package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

public record RequestedBookRequestDTO(
        @NotBlank
        String bookIsbn,
        @NotBlank
        String userEmail
) {
}