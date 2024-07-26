package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookAlreadyPresentException;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.validators.BookStatusTransitionValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * This service provides methods for managing {@link RequestedBook} entities.
 * <p>
 * This service provides methods to create, update, and delete {@link RequestedBook} records. It also handles
 * operations related to book status changes.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RequestedBookManagementServiceImpl implements RequestedBookManagementService {

    private final RequestedBookRepository requestedBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final RequestedBookConverter requestedBookConverter;
    private final BookStatusTransitionValidator bookStatusTransitionValidator;

    /**
     * Saves a new {@link RequestedBook} based on the provided ISBN.
     * <p>
     * Checks if the book with the given ISBN already exists in the database. If not, the method will
     * call an external service to fetch the book details and create a new {@link RequestedBook}.
     * </p>
     * <p>
     * Constraints:
     * <ul>
     *     <li>The book must not already be present in the database.</li>
     * </ul>
     * </p>
     *
     * @param bookISBN ISBN of the book to be requested.
     * @return {@link RequestedBookDTO} of the newly saved requested book.
     * @throws BookAlreadyPresentException If a book with the given ISBN already exists in the database.
     */
    @Override
    public RequestedBookDTO saveRequestedBook(String bookISBN) {
        Optional<Book> book = bookRepository.findByIsbn(bookISBN);

        if (book.isPresent()) {
            throw new BookAlreadyPresentException(bookISBN);
        }

        //todo: implement method from book service that uses google books api to fetch given book

        return null;
    }

    //todo: change javadoc, test, change by isbn
    /**
     * Deletes a {@link RequestedBook} by its ID.
     * <p>
     * Removes the requested book record from the database than the associated {@link Book} will also be deleted.
     * </p>
     *
     * @param requestedBookId ID of the requested book to be deleted.
     * @return {@code UUID} of the deleted requested book.
     * @throws RequestedBookNotFoundException If a requested book with the given ID does not exist.
     */
    @Override
    @Transactional
    public UUID deleteRequestedBookById(UUID requestedBookId) {
        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        Book book = requestedBook.getBook();
        String bookIsbn = book.getIsbn();
        bookRepository.deleteByIsbn(bookIsbn);

        return requestedBookId;
    }

    /**
     * Changes the status of a {@link RequestedBook}.
     * <p>
     * The method updates the status of the book associated with the requested book and validates the transition.
     * </p>
     * <p>
     * Constraints:
     * <ul>
     *     <li>The requested book must exist.</li>
     *     <li>Valid status transitions must be followed.</li>
     * </ul>
     * </p>
     *
     * @param requestedBookId UUID of the requested book.
     * @param newBookStatus   new status to set for the book.
     * @return {@link RequestedBookDTO} of the updated requested book.
     * @throws RequestedBookNotFoundException If a requested book with the given ID does not exist.
     * @throws RequestedBookStatusException   If the status transition is not allowed.
     */
    @Override
    public RequestedBookDTO changeBookStatus(UUID requestedBookId, BookStatus newBookStatus) {
        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        Book book = requestedBook.getBook();
        BookStatus currentBookStatus = book.getBookStatus();

        if (currentBookStatus == newBookStatus) {
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        validateBookStatusTransition(currentBookStatus, newBookStatus);
        book.setBookStatus(newBookStatus);
        bookRepository.save(book);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Adds or removes a like for a {@link RequestedBook} by a {@link User}.
     * <p>
     * This method updates the list of users who have liked the requested book and adjusts the like counter
     * accordingly.
     * </p>
     *
     * @param requestedBookId ID of the requested book.
     * @param userEmail       Email of the user liking or unliking the book.
     * @return {@link RequestedBookDTO} with the updated like counter information.
     * @throws RequestedBookNotFoundException If a requested book with the given ID does not exist.
     * @throws UserNotFoundException          If a user with the given email does not exist.
     */
    @Override
    public RequestedBookDTO handleRequestedBookLike(UUID requestedBookId, String userEmail) {
        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        Set<User> likedByUsers = requestedBook.getUsers();
        if (likedByUsers.contains(user)) {
            likedByUsers.remove(user);
        } else {
            likedByUsers.add(user);
        }
        requestedBook.refreshLikeCounter();
        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Validates the transition between book statuses.
     * <p>
     * Ensures that the transition from the current status to the new status is allowed based on predefined rules.
     * </p>
     *
     * @param currentBookStatus Current status of the book.
     * @param newBookStatus     New status to transition to.
     * @throws RequestedBookStatusException If the status transition is not valid.
     */
    private void validateBookStatusTransition(BookStatus currentBookStatus, BookStatus newBookStatus) {
        if (!bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus)) {
            throw new RequestedBookStatusException(currentBookStatus.name(), newBookStatus.name());
        }
    }

    private RequestedBook getRequestedBook(UUID requestedBookId) {
        return requestedBookRepository.findById(requestedBookId)
                .orElseThrow(() -> new RequestedBookNotFoundException(requestedBookId));
    }
}