package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.services.BookCheckoutQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link BookCheckoutQueryService} that includes methods for retrieving various views of
 * book checkout history. <br>
 * Access controls are specified for different operations.
 */
@Service
@RequiredArgsConstructor
public class BookCheckoutQueryServiceImpl implements BookCheckoutQueryService {
    private static final int DAYS_NEARING_THE_SCHEDULED_RETURN_DATE = 3;

    private final BookCheckoutRepository bookCheckoutRepository;
    private final BookCheckoutConverter bookCheckoutConverter;

    /**
     * This method is used to get a list of the book checkouts ordered by date borrowed in descending order.<br>
     * Only admin will have access to this method.
     *
     * @param officeName the name of the office where the user searching belongs.
     * @return A list containing {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckouts(String officeName) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findAll(officeName);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get a paginated list of all book checkouts ordered by date borrowed in descending order.<br>
     * Only admin users have access to this method.
     *
     * @param pageNumber the page number of the results to retrieve
     * @param pageSize      the maximum number of items per page
     * @param officeName    the name of the office where the user searching belongs.
     * @return A page containing {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsPaginated(int pageNumber,
                                                                                             int pageSize,
                                                                                             String officeName) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<BookCheckout> bookCheckouts = bookCheckoutRepository.findAllPaginated(officeName, pageable);

        return bookCheckouts.map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO);
    }

    /**
     * This method is used to get a list of all active book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @param officeName the name of the office where the user searching belongs.
     * @return A list containing {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllActiveBookCheckouts(String officeName) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findAllActiveCheckouts(officeName);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get a list of all past book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @param officeName the name of the office where the user searching belongs.
     * @return A list containing {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllPastBookCheckouts(String officeName) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findAllPastCheckouts(officeName);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get a list of all the book checkouts for a given user and book.<br>
     * All users will have access to this method, but to only see their book checkouts.
     *
     * @param userId              UUID for the id of the User.
     * @param bookTitleSearchTerm String value for the Title of the Book.
     * @return A list containing {@link BookCheckoutResponseDTO}
     */
    @Override
    public List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserForBook(UUID userId,
                                                                            String bookTitleSearchTerm) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findByUserAndBookTitleContaining(userId, bookTitleSearchTerm);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutResponseDTO).toList();
    }

    /**
     * This method is used to get a list of all the book checkouts for a given book by the provided titleSearchTerm.<br>
     * Only admin will have access.
     *
     * @param titleSearchTerm String value for the Title of the Book.
     * @param officeName      the name of the office where the user searching belongs.
     * @return A list containing {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookTitle(
            String officeName, String titleSearchTerm) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findByBookTitleContaining(officeName, titleSearchTerm);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get a list of all the book checkouts for a given user.<br>
     * All users will have access to this method, but to only see their bookCheckouts.
     *
     * @param userId UUID value for the id of the User.
     * @return A list containing {@link BookCheckoutResponseDTO}
     */
    @Override
    public List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserWithId(UUID userId) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findByUser(userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutResponseDTO).toList();
    }

    /**
     * This method is used to filter a list of all active bookCheckouts which return date is nearing.<br>
     * This will be accessed by the application for sending out notifications.
     *
     * @param officeName the name of the office where the user searching belongs.
     * @return A list containing {@link BookCheckoutReturnReminderResponseDTO}
     */
    @Override
    public List<BookCheckoutReturnReminderResponseDTO> getAllBookCheckoutsNearingReturnDate(String officeName) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findAllActiveCheckouts(officeName);

        return bookCheckouts.stream()
                .filter(bookCheckout -> isBookCheckoutNearingReturnDate(bookCheckout.getScheduledReturnDate()))
                .map(bookCheckoutConverter::toBookCheckoutReturnReminderResponseDTO)
                .toList();
    }

    private boolean isBookCheckoutNearingReturnDate(LocalDate scheduledReturnDate) {
        int daysBeforeReturn = (int) ChronoUnit.DAYS.between(LocalDate.now(), scheduledReturnDate);

        return daysBeforeReturn <= DAYS_NEARING_THE_SCHEDULED_RETURN_DATE;
    }
}
