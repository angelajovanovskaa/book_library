package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookCheckoutOnlyForUserProfileInfoResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutService {
    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckouts();

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllActiveBookCheckouts();

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllPastBookCheckouts();

    List<BookCheckoutOnlyForUserProfileInfoResponseDTO> getAllBookCheckoutsFromUserForBook(UUID userId,
                                                                                           String bookTitle);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookTitle(String title);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsFromUserWithFullName(
            String fullNameSearchTerm);

    List<BookCheckoutOnlyForUserProfileInfoResponseDTO> getAllBookCheckoutsFromUserWithId(UUID userId);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookISBN(String bookISBN);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookItem(UUID bookItemId);

    String borrowBookItem(BookCheckoutRequestDTO bookCheckoutDTO);

    String returnBookItem(BookCheckoutRequestDTO bookCheckoutDTO);

    String reportBookItemAsDamaged(UUID bookItemId);

    String reportBookItemAsLost(UUID bookItemId);

    List<BookCheckoutReturnReminderResponseDTO> getAllBookCheckoutsNearingReturnDate();

}
