package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface BookCheckoutQueryService {
    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckouts(String officeName);

    Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsPaginated(int pageNumber, int pageSize
            , String officeName);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllActiveBookCheckouts(String officeName);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllPastBookCheckouts(String officeName);

    List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserForBook(UUID userId, String bookTitleSearchTerm);

    List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookTitle(String officeName,
                                                                                         String titleSearchTerm);

    List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserWithId(UUID userId);

    List<BookCheckoutReturnReminderResponseDTO> getAllBookCheckoutsNearReturnDate(String officeName);
}
