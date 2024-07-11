package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;

import java.util.List;
import java.util.UUID;

public interface RequestedBookService {

    List<RequestedBookDTO> getAll();

    List<RequestedBookDTO> getAllRequestedBooksWithStatus(BookStatus status);

    RequestedBookDTO getRequestedBookById(UUID id);

    RequestedBookDTO getRequestedBookByISBN(String ISBN);

    List<RequestedBookDTO> getRequestedBookByTitle(String ISBN);

    RequestedBookDTO getFavoriteRequestedBook();

    RequestedBookDTO save(String bookISBN);

    RequestedBookDTO deleteRequestedBook(UUID requestedBookId);

    RequestedBookDTO changeStatus(UUID requestedBookId, BookStatus to);

    RequestedBookDTO enterRequestedBookInStock(UUID requestedBookId);
}