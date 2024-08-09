package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookQueryService {
    List<BookDisplayDTO> getAllBooks(String officeName);

    BookDTO getBookByIsbn(String isbn, String officeName);

    List<BookDisplayDTO> getBooksByTitle(String title, String officeName);

    List<BookDisplayDTO> getAvailableBooks(String officeName);

    List<BookDisplayDTO> getRequestedBooks(String officeName);

    List<BookDisplayDTO> getBooksByLanguage(String language, String officeName);

    List<BookDisplayDTO> getBooksByGenresContaining(String[] genres, String officeName);

    Page<BookDisplayDTO> getPaginatedAvailableBooks(BookStatus bookStatus, BookItemState bookItemState,
                                                    int page, int size, String officeName);
}
