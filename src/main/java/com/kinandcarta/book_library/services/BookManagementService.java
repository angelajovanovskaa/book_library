package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookIdRequestDTO;

public interface BookManagementService {
    BookDTO createBookWithAuthors(BookDTO bookDTO, String officeName);

    String deleteBook(String isbn, String officeName);

    BookDTO setBookStatusInStock(BookIdRequestDTO bookIdRequestDTO);
}
