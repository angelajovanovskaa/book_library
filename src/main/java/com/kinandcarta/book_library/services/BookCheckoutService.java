package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookCheckoutDTO;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutService {
    List<BookCheckoutDTO> getAllBookCheckouts();

    List<BookCheckoutDTO> getAllBookCheckoutsFromUserForBook(UUID userId, String bookISBN);

    List<BookCheckoutDTO> getAllBookCheckoutsForBookTitle(String title);

    List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithNameAndSurname(String name, String surname);

    List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithName(String name);

    List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithSurname(String surname);

    List<BookCheckoutDTO> getAllBookCheckoutsFromUserWithId(UUID userId);

    List<BookCheckoutDTO> getAllBookCheckoutsForBookISBN(String bookISBN);

    List<BookCheckoutDTO> getAllBookCheckoutsForBookItem(UUID bookItemId);

    BookCheckoutDTO borrowBookItem(BookCheckoutDTO bookCheckoutDTO);

    String returnBookItem(BookCheckoutDTO bookCheckoutDTO);

}
