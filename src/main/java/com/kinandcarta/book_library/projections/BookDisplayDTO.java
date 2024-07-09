package com.kinandcarta.book_library.projections;

import com.kinandcarta.book_library.enums.Language;
import lombok.Builder;

import java.util.Set;

@Builder
public record BookDisplayDTO(
        String ISBN,
        String title,
        String language,
        String image,
        Set<AuthorFullNameProjection> authors
) {
}
