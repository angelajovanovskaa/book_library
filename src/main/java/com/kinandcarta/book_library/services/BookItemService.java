package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.dtos.BookItemDTO;

import java.util.List;
import java.util.UUID;

public interface BookItemService {
    List<BookItemDTO> findByBookIsbn(String isbn);
    BookItemDTO insertBookItem(String isbn);
    UUID deleteById(UUID id);
    String reportBookItemAsDamaged(UUID bookItemId);
    String reportBookItemAsLost(UUID bookItemId);
}
