package com.kinandcarta.book_library.dtos;

import lombok.Builder;

import java.util.Set;

@Builder
public record BookDisplayDTO(
        String ISBN,
        String title,
        String language,
        String image,
        Set<AuthorDTO> authorDTOS
) {
}
