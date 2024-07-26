package com.kinandcarta.book_library.dtos;


public record BookDisplayDTO(
        String isbn,
        String title,
        String language,
        String image
) {
}
