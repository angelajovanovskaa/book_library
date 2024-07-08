package com.kinandcarta.book_library.projections;

import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Language;
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
        String summary,
        Language language,
        @Positive
        int totalPages,
        BookStatus bookStatus,
        String image,
        double ratingFromWeb,
        double ratingFromFirm,
        Set<AuthorFullNameProjection> authors,
        Set<Review> reviews

) {
}
