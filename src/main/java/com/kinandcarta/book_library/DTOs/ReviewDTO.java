package com.kinandcarta.book_library.DTOs;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.UUID;

public record ReviewDTO(

        UUID id,

        Date date,

        @NotBlank
        String message,

        @Min(1)
        @Max(10)
        Integer rating,

        String bookISBN,

        String userEmail
) {
}
