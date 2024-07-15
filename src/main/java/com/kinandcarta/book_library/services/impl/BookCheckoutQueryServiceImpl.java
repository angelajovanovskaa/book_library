package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.services.BookCheckoutQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookCheckoutQueryServiceImpl implements BookCheckoutQueryService {
    private static final int DAYS_NEARING_THE_SCHEDULED_RETURN_DATE = 3;

    private final BookCheckoutRepository bookCheckoutRepository;
    private final BookCheckoutConverter bookCheckoutConverter;

    /**
     * This method is used to get all of the book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckouts() {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findAll();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the active book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllActiveBookCheckouts() {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findByDateReturnedIsNull();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the past book checkouts.<br>
     * Only admin will have access to this method.
     *
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllPastBookCheckouts() {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findByDateReturnedIsNotNull();

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given user and book.<br>
     * All users will have access to this method, but to only see their book checkouts.
     *
     * @param userId              UUID value for the id of the User, cannot be {@code null}
     * @param bookTitleSearchTerm String value for the Title of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutResponseDTO}
     */
    @Override
    public List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserForBook(UUID userId,
                                                                            String bookTitleSearchTerm) {
        List<BookCheckout> bookCheckouts =
                bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseAndUserIdOrderByDateBorrowedDesc(
                        bookTitleSearchTerm,
                        userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutResponseDTO).toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given book by its titleSearchTerm.<br>
     * Only admin will have access.
     *
     * @param titleSearchTerm String value for the Title of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookTitle(
            String titleSearchTerm) {
        List<BookCheckout> bookCheckouts =
                bookCheckoutRepository.findByBookItem_Book_TitleContainingIgnoreCaseOrderByDateBorrowedDesc(
                        titleSearchTerm);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given user by its name and surname.<br>
     * The name and surname is sent as a fullNameSearchTerm string with an empty space between the two.
     * Only admin will have access to this method.
     *
     * @param fullNameSearchTerm String value for the name of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsFromUserWithFullName(
            String fullNameSearchTerm) {
        List<BookCheckout> bookCheckouts =
                bookCheckoutRepository.findByUser_FullNameContainingIgnoreCaseOrderByDateBorrowed(
                        fullNameSearchTerm);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given user.<br>
     * All users will have access to this method, but to only see their bookCheckouts.
     *
     * @param userId UUID value for the id of the User, cannot be {@code null}
     * @return List of {@link BookCheckoutResponseDTO}
     */
    @Override
    public List<BookCheckoutResponseDTO> getAllBookCheckoutsFromUserWithId(UUID userId) {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(userId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutResponseDTO).toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given book.<br>
     * Only admin will have access to this method.
     *
     * @param bookISBN String value for the ISBN of the Book, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookISBN(String bookISBN) {
        List<BookCheckout> bookCheckouts =
                bookCheckoutRepository.findByBookItem_Book_ISBNOrderByDateBorrowedDesc(bookISBN);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to get all of the book checkouts for a given bookItem.<br>
     * Only admin will have access to this method.
     *
     * @param bookItemId UUID value for the id of the BookItem, cannot be {@code null}
     * @return List of {@link BookCheckoutWithUserAndBookItemInfoResponseDTO}
     */
    @Override
    public List<BookCheckoutWithUserAndBookItemInfoResponseDTO> getAllBookCheckoutsForBookItem(UUID bookItemId) {
        List<BookCheckout> bookCheckouts =
                bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItemId);

        return bookCheckouts.stream().map(bookCheckoutConverter::toBookCheckoutWithUserAndBookItemInfoResponseDTO)
                .toList();
    }

    /**
     * This method is used to filter all of the active bookCheckouts which return date is nearing.<br>
     * This will be accessed by the application for sending out notifications.
     *
     * @return returns {@link BookCheckoutReturnReminderResponseDTO}
     */
    @Override
    public List<BookCheckoutReturnReminderResponseDTO> getAllBookCheckoutsNearingReturnDate() {
        List<BookCheckout> bookCheckouts = bookCheckoutRepository.findByDateReturnedIsNull();

        return bookCheckouts.stream()
                .filter(x -> isBookCheckoutNearingReturnDate(x.getScheduledReturnDate()))
                .map(bookCheckoutConverter::toBookCheckoutReturnReminderResponseDTO)
                .toList();
    }

    private boolean isBookCheckoutNearingReturnDate(LocalDate scheduledReturnDate) {
        int daysBeforeReturn = (int) ChronoUnit.DAYS.between(LocalDate.now(), scheduledReturnDate);

        return daysBeforeReturn <= DAYS_NEARING_THE_SCHEDULED_RETURN_DATE;
    }
}
