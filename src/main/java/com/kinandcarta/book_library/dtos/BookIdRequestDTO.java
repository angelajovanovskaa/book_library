package com.kinandcarta.book_library.dtos;

public record BookIdRequestDTO(
        String isbn,
        String officeName
) {
}
