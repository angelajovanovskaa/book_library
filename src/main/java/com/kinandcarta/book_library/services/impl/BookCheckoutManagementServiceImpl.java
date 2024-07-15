package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookCheckout;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link BookCheckoutManagementService} that manages the borrowing, returning, and status tracking of book items.</br>
 * This service includes methods for retrieving various views of book checkout history and managing book item states.
 * Access controls are specified for different operations.
 */
@Service
@RequiredArgsConstructor
public class BookCheckoutManagementServiceImpl implements BookCheckoutManagementService {
    private static final int MAX_NUMBER_OF_BORROWED_BOOKS = 3;

    private final BookCheckoutRepository bookCheckoutRepository;
    private final BookItemRepository bookItemRepository;
    private final UserRepository userRepository;
    private final BookReturnDateCalculatorService calculatorService;

    /**
     * This method is used to borrow a book from the library.<br>
     * For this method to perform a successful borrowing of a book, the following constraints must be satisfied by the user:
     * <ul>
     *     <li>Check if the user has reached the limit for borrowed books.</li>
     *     <li>Check if the book item being borrowed is available.</li>
     *     <li>Ensure the user does not already have an instance of the same book borrowed.</li>
     *     <li>Verify if enough time has passed for the user to borrow the same book again.</li>
     * </ul>
     * If all constraints are met, the book is borrowed and the {@code dateBorrowed} and {@code scheduledReturnDate}
     * dates are set.
     *
     * @param bookCheckoutDTO The DTO containing userId and bookItemId for the book to be borrowed, cannot be
     *                        {@code null}.
     * @return A message indicating that the book was successfully borrowed.
     * @throws LimitReachedForBorrowedBooksException If the user has already borrowed the maximum number of books.
     * @throws UserNotFoundException                 If the specified userId does not correspond to any user in the
     *                                               repository.
     * @throws BookItemNotFoundException             If the specified bookItemId does not correspond to any book item
     *                                               in the repository.
     * @throws BookItemAlreadyBorrowedException      If the book item is already borrowed by another user.
     * @throws BookAlreadyBorrowedByUserException    If the user already has an instance of the same book borrowed.
     */
    @Override
    @Transactional
    public String borrowBookItem(BookCheckoutRequestDTO bookCheckoutDTO) {
        UUID userId = bookCheckoutDTO.userId();
        if (isBorrowedBooksLimitReached(userId)) {
            throw new LimitReachedForBorrowedBooksException(MAX_NUMBER_OF_BORROWED_BOOKS);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        UUID bookItemId = bookCheckoutDTO.bookItemId();
        BookItem bookItem = bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        if (bookItem.getBookItemState() == BookItemState.BORROWED) {
            throw new BookItemAlreadyBorrowedException(bookItemId);
        }

        Book book = bookItem.getBook();
        if (hasInstanceOfBookBorrowed(userId, bookItem)) {
            throw new BookAlreadyBorrowedByUserException(book.getISBN());
        }

        LocalDate scheduledReturnDate = calculatorService.calculateReturnDateOfBookItem(bookItem);

        BookCheckout bookCheckout = new BookCheckout();
        bookCheckout.setUser(user);
        bookCheckout.setBookItem(bookItem);
        bookCheckout.setDateBorrowed(LocalDate.now());
        bookCheckout.setScheduledReturnDate(scheduledReturnDate);
        bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.BORROWED);
        bookItemRepository.save(bookItem);

        return "You have successfully borrowed the book " + book.getTitle();
    }

    /**
     * This method is used to return a borrowed book to the library.<br>
     * This method handles the process of returning a book item by marking it as returned in the system
     * and updating its state to AVAILABLE. It also checks if the return was on time or overdue.
     *
     * @param bookCheckoutDTO The DTO containing the bookItemId of the book item to be returned, cannot be {@code null}.
     * @return A message indicating the status of the book return:
     * <ul>
     *     <li>If the book return is overdue, it returns a message specifying how many days it is overdue.</li>
     *     <li>If the book is returned on the scheduled return date, it returns a message indicating this.</li>
     *     <li>If the book is returned before the scheduled return date, it returns a message indicating this.
     *     </li>
     * </ul>
     * @throws InvalidReturnBookItemRequestException If the bookItemId in the provided DTO is {@code null}.
     * @throws BookItemNotFoundException             If the specified bookItemId does not correspond to any book item in the
     *                                               repository.
     * @throws BookItemIsNotBorrowedException        If the book item identified by bookItemId is not currently borrowed.
     */
    @Override
    @Transactional
    public String returnBookItem(BookCheckoutRequestDTO bookCheckoutDTO) {
        UUID bookItemId = bookCheckoutDTO.bookItemId();
        BookItem bookItem = bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        BookCheckout bookCheckout =
                bookCheckoutRepository.findByBookItemIdOrderByDateBorrowedDesc(bookItemId)
                        .stream()
                        .filter(x -> x.getDateReturned() == null)
                        .findFirst()
                        .orElseThrow(() -> new BookItemIsNotBorrowedException(bookItemId));

        LocalDate dateReturned = LocalDate.now();
        bookCheckout.setDateReturned(dateReturned);
        bookCheckoutRepository.save(bookCheckout);

        bookItem.setBookItemState(BookItemState.AVAILABLE);
        bookItemRepository.save(bookItem);

        LocalDate scheduledReturnDate = bookCheckout.getScheduledReturnDate();

        return resolveBookCheckoutResponseMessage(scheduledReturnDate, dateReturned);
    }

    private boolean isBorrowedBooksLimitReached(UUID userId) {
        List<BookCheckout> listOfBookCheckoutsWithUser =
                bookCheckoutRepository.findByUserIdOrderByDateBorrowedDesc(userId);

        long countInstancesOfUser = listOfBookCheckoutsWithUser.stream()
                .filter(x -> x.getDateReturned() == null)
                .count();

        return countInstancesOfUser == MAX_NUMBER_OF_BORROWED_BOOKS;
    }

    private boolean hasInstanceOfBookBorrowed(UUID userId, BookItem bookItem) {
        Book book = bookItem.getBook();

        List<BookCheckout> bookCheckoutsForUserAndBook =
                bookCheckoutRepository.findByBookItem_Book_ISBNAndUserIdOrderByDateBorrowedDesc(
                        book.getISBN(), userId);

        return bookCheckoutsForUserAndBook.stream()
                .anyMatch(x -> x.getDateReturned() == null);
    }

    private String resolveBookCheckoutResponseMessage(LocalDate scheduledReturnDate, LocalDate dateReturned) {
        if (scheduledReturnDate.isBefore(dateReturned)) {
            int daysDifference = (int) ChronoUnit.DAYS.between(scheduledReturnDate, dateReturned);

            return "The book return is overdue by "
                    + daysDifference + " day(s), next time be more careful about the scheduled return date.";
        } else if (scheduledReturnDate.isEqual(dateReturned)) {
            return "The book is returned on the scheduled return date.";
        } else {
            return "The book is returned before the scheduled return date.";
        }
    }

}
