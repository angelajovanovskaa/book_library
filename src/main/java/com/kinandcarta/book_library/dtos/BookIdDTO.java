package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotNull;

public record BookIdDTO(
        @NotNull
        String isbn,
        @NotNull
        String officeName
) {
}
