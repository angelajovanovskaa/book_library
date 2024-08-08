package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookItemDTO;

import java.util.UUID;

public interface BookItemManagementService {
    BookItemDTO insertBookItem(String isbn, String officeName);

    UUID deleteById(UUID id);

    String reportBookItemAsDamaged(UUID bookItemId);

    String reportBookItemAsLost(UUID bookItemId);
}
