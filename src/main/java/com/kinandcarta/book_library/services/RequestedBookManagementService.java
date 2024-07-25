package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.enums.BookStatus;

import java.util.UUID;

public interface RequestedBookManagementService {
    //todo: create test for method saveRequestedBook()
    RequestedBookDTO saveRequestedBook(String bookISBN);

    UUID deleteRequestedBookById(UUID requestedBookId);

    RequestedBookDTO changeBookStatus(UUID requestedBookId, BookStatus to);

    RequestedBookDTO handleRequestedBookLike(UUID requestedBookId, String userEmail);
}