package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.*;

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