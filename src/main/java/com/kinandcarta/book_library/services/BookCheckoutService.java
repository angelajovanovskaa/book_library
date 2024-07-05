package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookCheckoutComplexResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutSchedulerResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutSimpleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutService {
    List<BookCheckoutComplexResponseDTO> getAllBookCheckouts();

    List<BookCheckoutComplexResponseDTO> getAllActiveBookCheckouts();

    List<BookCheckoutComplexResponseDTO> getAllPastBookCheckouts();

    List<BookCheckoutSimpleResponseDTO> getAllBookCheckoutsFromUserForBook(UUID userId, String bookTitle);

    List<BookCheckoutComplexResponseDTO> getAllBookCheckoutsForBookTitle(String title);

    List<BookCheckoutComplexResponseDTO> getAllBookCheckoutsFromUserWithFullName(String fullName);

    List<BookCheckoutSimpleResponseDTO> getAllBookCheckoutsFromUserWithId(UUID userId);

    List<BookCheckoutComplexResponseDTO> getAllBookCheckoutsForBookISBN(String bookISBN);

    List<BookCheckoutComplexResponseDTO> getAllBookCheckoutsForBookItem(UUID bookItemId);

    String borrowBookItem(BookCheckoutRequestDTO bookCheckoutDTO);

    String returnBookItem(BookCheckoutRequestDTO bookCheckoutDTO);

    String reportBookItemAsDamaged(UUID bookItemId);

    String reportBookItemAsLost(UUID bookItemId);

    List<BookCheckoutSchedulerResponseDTO> getAllBookCheckoutsNearingReturnDate();

}
