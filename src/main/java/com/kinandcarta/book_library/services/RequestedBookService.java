package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.enums.BookStatus;

import java.util.List;
import java.util.UUID;

public interface RequestedBookService {

    List<RequestedBookDTO> getAllRequestedBooks();

    List<RequestedBookDTO> filterRequestedBooksByBookStatus(BookStatus status);

    RequestedBookDTO getRequestedBookById(UUID id);

    RequestedBookDTO getRequestedBookByISBN(String isbn);

    RequestedBookDTO saveRequestedBook(String bookISBN);

    UUID deleteRequestedBookById(UUID requestedBookId);

    RequestedBookDTO changeBookStatus(UUID requestedBookId, BookStatus to);

    RequestedBookDTO enterRequestedBookInStock(UUID requestedBookId);

    RequestedBookDTO handleRequestedBookLike(UUID requestedBookId, String userEmail);
}