package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;

public interface RequestedBookManagementService {
    //todo: create test for method saveRequestedBook()
    RequestedBookResponseDTO saveRequestedBook(RequestedBookRequestDTO requestedBookRequestDTO);

    RequestedBookResponseDTO changeBookStatus(RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO);

    RequestedBookResponseDTO handleRequestedBookLike(RequestedBookRequestDTO requestedBookRequestDTO);
}