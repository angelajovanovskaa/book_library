package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookCheckoutConverter;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.entities.*;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.exceptions.*;
import com.kinandcarta.book_library.repositories.BookCheckoutRepository;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.BookCheckoutManagementService;
import com.kinandcarta.book_library.services.BookReturnDateCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link BookCheckoutManagementService} that manages the borrowing and returning of a book<br>
 * Access controls are specified for different operations.
 */
@Service
@RequiredArgsConstructor
public class BookCheckoutManagementServiceImpl implements BookCheckoutManagementService {
    private static final int MAX_NUMBER_OF_BORROWED_BOOKS = 3;

    private final BookCheckoutRepository bookCheckoutRepository;
    private final BookCheckoutConverter bookCheckoutConverter;
    private final BookItemRepository bookItemRepository;
    private final UserRepository userRepository;
    private final BookReturnDateCalculatorService bookReturnDateCalculatorService;

    /**
     * This method is used to borrow a bookItem from the library.<br>
     * For this method to perform a successful borrowing of a bookItem, the following constraints must be satisfied by
     * the user:
     * <ul>
     *     <li>Check if the bookItem that is given exists in the database</li>
     *     <li>Check if the user has reached the limit for borrowed books.</li>
     *     <li>Check if the book item being borrowed is available.</li>
     *     <li>Ensure the user does not already have an instance of the same book borrowed.</li>
     *     <li>Verify if the user is borrowing a bookItem from the office that he works in.</li>
     * </ul>
     * If all constraints are met, the book is borrowed and the {@code dateBorrowed} and {@code scheduledReturnDate}
     * dates are set, also the {@code bookItemState} is set to BORROWED.
     *
     * @param bookCheckoutDTO The DTO containing userId and bookItemId for the bookItem to be borrowed.
     * @return {@link BookCheckoutResponseDTO}
     * @throws BookItemNotFoundException             If the specified bookItemId does not correspond to any book item
     *                                               in the repository.
     * @throws EntitiesInDifferentOfficesException   if the user tries to borrow a book from a different office.
     * @throws BookItemAlreadyBorrowedException      If the book item is already borrowed by another user.
     * @throws BookAlreadyBorrowedByUserException    If the user already has an instance of the same book borrowed.
     * @throws LimitReachedForBorrowedBooksException If the user has already borrowed the maximum number of books.
     */
    @Override
    @Transactional
    public BookCheckoutResponseDTO borrowBookItem(BookCheckoutRequestDTO bookCheckoutDTO) {
        UUID bookItemId = bookCheckoutDTO.bookItemId();
        UUID userId = bookCheckoutDTO.userId();

        BookItem bookItem = bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        User user = userRepository.getReferenceById(userId);
        Office userOffice = user.getOffice();
        Book book = bookItem.getBook();

        validateBorrowingConditions(bookItem, user, userOffice, book);

        BookCheckout bookCheckout = createAndSaveBookCheckout(bookItem, user, userOffice);

        return bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckout);
    }

    /**
     * This method is used to return a borrowed book to the library.<br>
     * This method handles the process of returning a book item by marking it as returned in the system
     * and updating its state to AVAILABLE. It also checks if the return was on time or overdue.
     *
     * @param bookCheckoutDTO The DTO containing userId and bookItemId for the bookItem to be returned.
     * @return {@link BookCheckoutResponseDTO}
     * @throws BookItemNotFoundException      If the specified bookItemId does not correspond to any book item in the
     *                                        repository.
     * @throws BookItemIsNotBorrowedException If the book item identified by bookItemId is not currently borrowed.
     */
    @Override
    @Transactional
    public BookCheckoutResponseDTO returnBookItem(BookCheckoutRequestDTO bookCheckoutDTO) {
        UUID bookItemId = bookCheckoutDTO.bookItemId();
        BookItem bookItem = bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        BookCheckout bookCheckout = bookCheckoutRepository.findFirstByBookItemIdAndDateReturnedIsNull(bookItemId)
                .orElseThrow(() -> new BookItemIsNotBorrowedException(bookItemId));

        LocalDate dateReturned = LocalDate.now();
        bookCheckout.setDateReturned(dateReturned);
        bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.AVAILABLE);
        bookItemRepository.save(bookItem);

        return bookCheckoutConverter.toBookCheckoutResponseDTO(bookCheckout);
    }

    private boolean isBorrowedBooksLimitReached(UUID userId) {
        List<BookCheckout> listOfBookCheckoutsWithUser = bookCheckoutRepository.findByUser(userId);

        long countInstancesOfUser = listOfBookCheckoutsWithUser.stream()
                .filter(bookCheckout -> bookCheckout.getDateReturned() == null)
                .count();

        return countInstancesOfUser >= MAX_NUMBER_OF_BORROWED_BOOKS;
    }

    private boolean hasInstanceOfBookBorrowed(UUID userId, Book book) {
        Optional<BookCheckout> bookCheckoutOptional =
                bookCheckoutRepository.findFirstByBookItem_Book_IsbnAndUserIdAndDateReturnedIsNull(
                        book.getIsbn(), userId);

        return bookCheckoutOptional.isPresent();
    }

    private void validateBorrowingConditions(BookItem bookItem, User user, Office userOffice, Book book) {
        UUID bookItemId = bookItem.getId();
        UUID userId = user.getId();

        if (userOffice != book.getOffice()) {
            throw new EntitiesInDifferentOfficesException();
        }

        if (bookItem.getBookItemState() == BookItemState.BORROWED) {
            throw new BookItemAlreadyBorrowedException(bookItemId);
        }

        if (hasInstanceOfBookBorrowed(userId, book)) {
            throw new BookAlreadyBorrowedByUserException(book.getIsbn());
        }

        if (isBorrowedBooksLimitReached(userId)) {
            throw new LimitReachedForBorrowedBooksException(MAX_NUMBER_OF_BORROWED_BOOKS);
        }
    }

    private BookCheckout createAndSaveBookCheckout(BookItem bookItem, User user, Office userOffice) {
        BookCheckout bookCheckout = new BookCheckout();
        bookCheckout.setUser(user);
        bookCheckout.setBookItem(bookItem);
        bookCheckout.setOffice(userOffice);
        bookCheckout.setDateBorrowed(LocalDate.now());

        LocalDate scheduledReturnDate =
                bookReturnDateCalculatorService.calculateReturnDateOfBookItem(bookItem.getBook().getTotalPages());

        bookCheckout.setScheduledReturnDate(scheduledReturnDate);
        bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.BORROWED);
        bookItemRepository.save(bookItem);

        return bookCheckout;
    }
}
