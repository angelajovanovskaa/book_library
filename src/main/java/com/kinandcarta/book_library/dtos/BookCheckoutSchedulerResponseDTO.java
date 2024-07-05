package com.kinandcarta.book_library.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record BookCheckoutSchedulerResponseDTO(
        UUID userId,
        String bookTitle,
        LocalDate scheduledReturn
) {
}
