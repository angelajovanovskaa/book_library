package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutQueryService {
    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckouts();

    Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsPaginated(int numberOfPages, int pageSize);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllActiveBookCheckouts();

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllPastBookCheckouts();

    List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserForBook(UUID userId,
                                                                     String bookTitle);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookTitle(String title);

    List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserWithId(UUID userId);

    List<BookCheckoutReturnReminderResponseDTO> getAllBookCheckoutsNearingReturnDate();
}
