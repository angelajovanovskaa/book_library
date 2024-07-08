package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.projections.BookItemDTO;

import java.util.List;
import java.util.UUID;

public interface BookItemService {
    List<BookItemDTO> findByBookIsbn(String isbn);
    BookItem create(BookItemDTO bookItemDTO);
    UUID deleteById(UUID id);

}
