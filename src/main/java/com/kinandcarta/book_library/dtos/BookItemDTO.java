package com.kinandcarta.book_library.dtos;


import java.util.UUID;

public record BookItemDTO(
        String ISBN,
        UUID id
) {
}
