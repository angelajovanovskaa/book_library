package com.kinandcarta.book_library.dtos;


public record BookDisplayDTO(
        String ISBN,
        String title,
        String language,
        String image
) {
}
