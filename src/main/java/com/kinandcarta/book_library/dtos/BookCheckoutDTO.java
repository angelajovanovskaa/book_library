package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record BookCheckoutDTO(
        UUID id,
        @NotBlank
        UUID userId,
        @NotBlank
        UUID bookItemId,
        LocalDate dateBorrowed,
        LocalDate dateReturned,
        LocalDate scheduledReturn
) {
}
