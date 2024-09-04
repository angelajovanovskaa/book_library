package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.enums.BookStatus;

import java.util.List;
import java.util.UUID;

public interface RequestedBookQueryService {
    List<RequestedBookResponseDTO> getRequestedBooksByOfficeName(String officeName);

    List<RequestedBookResponseDTO> getRequestedBooksByBookStatusAndOfficeName(BookStatus status, String officeName);

    RequestedBookResponseDTO getRequestedBookById(UUID id);

    RequestedBookResponseDTO getRequestedBookByIsbnAndOfficeName(String isbn, String officeName);
}