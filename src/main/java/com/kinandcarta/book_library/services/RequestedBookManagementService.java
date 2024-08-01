package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.enums.BookStatus;

import java.util.UUID;

public interface RequestedBookManagementService {
    //todo: create test for method saveRequestedBook()
    RequestedBookResponseDTO saveRequestedBook(RequestedBookRequestDTO requestedBookRequestDTO);

    String deleteRequestedBookByBookIsbnAndOfficeName(String bookIsbn, String officeName);

    RequestedBookResponseDTO changeBookStatus(UUID requestedBookId, BookStatus to);

    RequestedBookResponseDTO handleRequestedBookLike(UUID requestedBookId, String userEmail);
}