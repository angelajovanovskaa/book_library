package com.kinandcarta.book_library.projections;

import com.kinandcarta.book_library.enums.BookItemState;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BookItemDTO(
        UUID id,
        String ISBN,
        BookItemState bookItemState,
        byte[] barcode
) {
}
