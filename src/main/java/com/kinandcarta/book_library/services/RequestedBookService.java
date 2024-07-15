package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;

import java.util.List;
import java.util.UUID;

public interface RequestedBookService {

    List<RequestedBookDTO> getAll();

    List<RequestedBookDTO> getAllRequestedBooksWithStatus(BookStatus status);

    List<RequestedBookDTO> filterRequestedBooks(String type, String input, BookStatus status);

    RequestedBookDTO getRequestedBookById(UUID id);

    RequestedBookDTO getRequestedBookByISBN(String ISBN);

    List<RequestedBookDTO> getTop3FavouriteRequestedBooks();

    RequestedBookDTO saveRequestedBook(String bookISBN);

    UUID deleteRequestedBookById(UUID requestedBookId);

    RequestedBookDTO changeBookStatus(UUID requestedBookId, BookStatus to);

    RequestedBookDTO enterRequestedBookInStock(UUID requestedBookId);

    RequestedBookDTO likeRequestedBook(UUID requestedBookId, String userEmail);
}