package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    List<BookDTO> getAllBooks();
    BookDTO getBookByIsbn(String isbn);
    List<BookDTO> getBooksByTitle(String title);
    List<BookDisplayDTO> getAvailableBooks();
    List<BookDisplayDTO> getRequestedBooks();
    List<BookDisplayDTO> getBooksByLanguage(String language);
    List<BookDisplayDTO> getBooksByGenresContaining(String[] genres);
    BookDTO createBookWithAuthors(BookDTO bookDTO);
    String deleteBook(String isbn);
    BookDTO setBookStatusInStock(String isbn);
    Page<BookDisplayDTO> getPaginatedAvailableBooks(BookStatus bookStatus, BookItemState bookItemState,
                                                    int page, int size);
}
