package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.BookItemState;

import java.util.UUID;


public record BookItemDTO(
        UUID id,
        String ISBN,
        BookItemState bookItemState
) {
}
