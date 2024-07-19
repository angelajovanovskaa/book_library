package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record BookCheckoutRequestDTO(
        @NotBlank
        UUID userId,
        @NotBlank
        UUID bookItemId
) {
}
