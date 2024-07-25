package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDTO(
        @NotNull
        String bookISBN,

        @NotNull
        String userEmail,

        @NotEmpty
        String message,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rating
) {
}