package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDTO(
        @NotBlank
        String bookISBN,

        @NotBlank
        String userEmail,

        @NotBlank
        String message,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rating
) {
}