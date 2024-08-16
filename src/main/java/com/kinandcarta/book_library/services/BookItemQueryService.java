package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookItemDTO;

import java.util.List;

public interface BookItemQueryService {
    List<BookItemDTO> getBookItemsByBookIsbn(String isbn, String officeName);
}
