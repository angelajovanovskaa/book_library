package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;

public record ReviewResponseDTO(
        String bookISBN,
        String userEmail,
        LocalDate date,
        String message,
        int rating
) {
}
