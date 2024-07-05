package com.kinandcarta.book_library.services;

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

    RequestedBookDTO delete(UUID recommendedBookId);

    RequestedBookDTO setStatusToPendingPurchase(UUID recommendedBookId);

    RequestedBookDTO setStatusToRequestedBook(UUID recommendedBookId);

    RequestedBookDTO setStatusToRejected(UUID recommendedBookId);
}