package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;

import java.util.List;

public interface BookService {
    List<BookDTO> getAllBooks();
    BookDTO getBookByIsbn(String isbn);
    List<BookDTO> getBooksByTitle(String title);
    List<BookDisplayDTO> filterAvailableBooks();
    List<BookDisplayDTO> filterRequestedBooks();
    List<BookDisplayDTO> getBooksByLanguage(String language);
    List<BookDisplayDTO> getBooksByGenresContaining(String[] genres);
    BookDTO createBookWithAuthors(BookDTO bookDTO);
    String deleteBook(String isbn);
    BookDTO setBookStatusInStock(String isbn);
}
