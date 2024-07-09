package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.projections.BookDTO;
import com.kinandcarta.book_library.projections.BookDisplayDTO;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<BookDTO> findAll();
    Optional<BookDTO> findBookByISBN(String ISBN);
    List<BookDTO> findBooksByTitle(String title);
    List<BookDisplayDTO> filterAvailableBooks(BookStatus bookStatus, BookItemState bookItemState);
    List<BookDisplayDTO> findBooksByBookStatusRequested(BookStatus bookStatus);
    List<BookDisplayDTO> findBooksByLanguage(String language);
    List<BookDisplayDTO> findBooksByGenresContaining(String[] genres);
    BookDTO create(BookDTO bookDTO);
    String delete(String ISBN);
    Optional<BookDTO> setBookStatusInStock(Book book);
}
