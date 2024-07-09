package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;

public record BookCheckoutResponseDTO(
        String bookTitle,
        String bookISBN,
        LocalDate dateBorrowed,
        LocalDate dateReturned,
        LocalDate scheduledReturnDate
) {
}
