package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record BookCheckoutReturnReminderResponseDTO(
        UUID userId,
        String bookTitle,
        LocalDate scheduledReturnDate
) {
}
