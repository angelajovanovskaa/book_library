package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;

public interface BookManagementService {
    BookDTO createBookWithAuthors(BookDTO bookDTO);

    BookIdDTO deleteBook(String isbn, String officeName);

    BookDTO setBookStatusInStock(BookIdDTO bookIdDTO);
}
