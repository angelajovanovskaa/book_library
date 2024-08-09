package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookItemDTO;

import java.util.UUID;

public interface BookItemManagementService {
    BookItemDTO insertBookItem(BookIdDTO bookIdDTO);

    UUID deleteById(UUID id);

    String reportBookItemAsDamaged(UUID bookItemId);

    String reportBookItemAsLost(UUID bookItemId);
}
