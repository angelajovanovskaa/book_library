package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;

public interface BookCheckoutManagementService {
    BookCheckoutResponseDTO borrowBookItem(BookCheckoutRequestDTO bookCheckoutDTO);

    BookCheckoutResponseDTO returnBookItem(BookCheckoutRequestDTO bookCheckoutDTO);
}
