package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.projections.BookCheckoutDTO;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutService {
    List<BookCheckoutDTO> getAllBookCheckouts();

    List<BookCheckoutDTO> getAllBookCheckoutsFromUserForBook(UUID userId, String bookISBN);

    List<BookCheckoutDTO> getAllBookCheckoutsFromUser(UUID userId);

    List<BookCheckoutDTO> getAllBookCheckoutsForBook(String bookISBN);

    List<BookCheckoutDTO> getAllBookCheckoutsForBookItem(UUID bookItemId);

    BookCheckoutDTO borrowBookItem(BookCheckoutDTO bookCheckoutDTO);

    String returnBookItem(BookCheckoutDTO bookCheckoutDTO);

}
