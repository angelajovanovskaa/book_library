package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewResponseDTO(
        UUID reviewId,
        String bookISBN,
        String userEmail,
        LocalDate date,
        String message,
        int rating
) {
}
