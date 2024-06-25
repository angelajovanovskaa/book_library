package com.kinandcarta.book_library.projections;

import com.kinandcarta.book_library.enums.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BookDTO (
        String ISBN,
        String title,
        String description,
        Language language,
        String image,
        int totalPages
) {
}
