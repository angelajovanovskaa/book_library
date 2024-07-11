package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;

public interface BookCheckoutManagementService {
    String borrowBookItem(BookCheckoutRequestDTO bookCheckoutDTO);

    String returnBookItem(BookCheckoutRequestDTO bookCheckoutDTO);
}
