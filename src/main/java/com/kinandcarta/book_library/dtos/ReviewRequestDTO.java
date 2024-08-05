package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequestDTO(
        @NotBlank
        String bookISBN,

        @NotBlank
        String userEmail,

        @NotBlank
        String message,

        @Min(1)
        @Max(5)
        int rating
) {
}