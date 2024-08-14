package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;

public interface BookManagementService {
    BookDisplayDTO createBookWithAuthors(BookInsertRequestDTO bookInsertRequestDTO);

    BookIdDTO deleteBook(String isbn, String officeName);

    BookDetailsDTO setBookStatusInStock(BookIdDTO bookIdDTO);
}
