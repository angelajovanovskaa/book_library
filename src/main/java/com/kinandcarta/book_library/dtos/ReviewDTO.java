package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewDTO(

        UUID id,

        LocalDate date,

        @NotBlank
        String message,

        @Min(1)
        @Max(5)
        Integer rating,

        String bookISBN,

        String userEmail
) {
}
