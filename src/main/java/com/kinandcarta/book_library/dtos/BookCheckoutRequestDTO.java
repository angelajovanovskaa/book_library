package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookCheckoutRequestDTO(
        @NotNull
        UUID userId,
        @NotNull
        UUID bookItemId
) {
}
