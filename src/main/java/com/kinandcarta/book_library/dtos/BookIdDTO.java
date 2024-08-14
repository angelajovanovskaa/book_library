package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

public record BookIdDTO(
        @NotBlank
        String isbn,
        @NotBlank
        String officeName
) {
}
