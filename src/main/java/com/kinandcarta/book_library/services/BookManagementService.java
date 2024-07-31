package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.entities.keys.BookId;

public interface BookManagementService {
    BookDTO createBookWithAuthors(BookDTO bookDTO, String officeName);
    String deleteBook(BookId id);
    BookDTO setBookStatusInStock(String isbn, String officeName);
}
