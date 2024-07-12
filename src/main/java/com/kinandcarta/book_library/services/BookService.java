package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;

import java.util.List;

public interface BookService {
    List<BookDTO> findAll();
    BookDTO findBookByIsbn(String isbn);
    List<BookDTO> findBooksByTitle(String title);
    List<BookDisplayDTO> filterAvailableBooks();
    List<BookDisplayDTO> filterRequestedBooks();
    List<BookDisplayDTO> findBooksByLanguage(String language);
    List<BookDisplayDTO> findBooksByGenresContaining(String[] genres);
    BookDTO createBookWithAuthors(BookDTO bookDTO);
    String deleteBook(String isbn);
    BookDTO setBookStatusInStock(String isbn);
}
