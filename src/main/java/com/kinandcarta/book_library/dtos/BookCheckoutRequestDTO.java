package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BookCheckoutRequestDTO(
        @NotBlank
        UUID userId,
        @NotBlank
        UUID bookItemId
) {
}
