package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.enums.BookStatus;

import java.util.List;
import java.util.UUID;

public interface RequestedBookQueryService {
    List<RequestedBookDTO> getAllRequestedBooks();

    List<RequestedBookDTO> getRequestedBooksByBookStatus(BookStatus status);

    RequestedBookDTO getRequestedBookById(UUID id);

    RequestedBookDTO getRequestedBookByISBN(String isbn);
}