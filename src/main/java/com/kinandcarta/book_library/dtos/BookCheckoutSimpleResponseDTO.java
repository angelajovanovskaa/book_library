package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;

public record BookCheckoutSimpleResponseDTO(
        String bookTitle,
        String bookISBN,
        LocalDate dateBorrowed,
        LocalDate dateReturned,
        LocalDate scheduledReturn
) {
}
