package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record BookCheckoutWithUserAndBookItemInfoResponseDTO(
        String userFullName,
        UUID bookItemId,
        String bookTitle,
        String bookISBN,
        LocalDate dateBorrowed,
        LocalDate dateReturned,
        LocalDate scheduledReturnDate
) {
}
