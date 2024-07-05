package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record BookCheckoutComplexResponseDTO(
        UUID id,
        UUID userId,
        UUID bookItemId,
        String bookTitle,
        String bookISBN,
        LocalDate dateBorrowed,
        LocalDate dateReturned,
        LocalDate scheduledReturn
) {
}
