package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.Set;

@Builder
public record BookDTO(
        String ISBN,
        @NotBlank
        String title,
        @NotBlank
        String description,
        String language,
        @Positive
        int totalPages,
        BookStatus bookStatus,
        String image,
        double ratingFromWeb,
        double ratingFromFirm,
        Set<AuthorDTO> authorDTOS
//        Set<Review> reviews

) {
}
