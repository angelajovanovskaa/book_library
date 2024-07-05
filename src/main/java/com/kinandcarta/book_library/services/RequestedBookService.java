package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.projections.RequestedBookDTO;

import java.util.List;
import java.util.UUID;

public interface RequestedBookService {

    List<RequestedBookDTO> getAll();

    List<RequestedBookDTO> getAllRequestedBooks();

    List<RequestedBookDTO> getAllPendingRequestedBooks();

    RequestedBookDTO getRequestedBookById(UUID id);

    RequestedBookDTO getRequestedBookByISBN(String ISBN);

    RequestedBookDTO getRequestedBookByTitle(String ISBN);

    RequestedBookDTO getFavoriteRequestedBook();

    RequestedBookDTO save(String bookISBN);

    RequestedBookDTO delete(UUID requestedBookId);

    RequestedBookDTO setStatusToPendingPurchase(UUID requestedBookId);

    RequestedBookDTO setStatusToRequestedBook(UUID requestedBookId);

    RequestedBookDTO setStatusToRejected(UUID requestedBookId);

    RequestedBookDTO changeStatus(UUID requestedBookId, BookStatus to);

    RequestedBookDTO buyRequestedBook(UUID requestedBookId);
}