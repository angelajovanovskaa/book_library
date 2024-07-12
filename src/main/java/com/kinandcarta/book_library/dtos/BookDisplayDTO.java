package com.kinandcarta.book_library.dtos;

import java.util.Set;

public record BookDisplayDTO(
        String ISBN,
        String title,
        String language,
        String image,
        Set<AuthorDTO> authorDTOS
) {
}
