package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
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
@RequiredArgsConstructor
@Service
public class RequestedBookManagementServiceImpl implements RequestedBookManagementService {

    private final RequestedBookRepository requestedBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final RequestedBookConverter requestedBookConverter;
    private final BookStatusTransitionValidator bookStatusTransitionValidator;

    /**
     * Saves a new {@link RequestedBook} based on the provided ISBN for the specified {@link Office}.
     * <p>
     * Checks if the book with the given ISBN for the given office already exists in the database. If not, the method
     * will call an external service to fetch the book details and create a new {@link RequestedBook}.
     * </p>
     * <p>
     * Constraints:
     * <ul>
     *     <li>The book must not already be present in the database.</li>
     * </ul>
     * </p>
     *
     * @param requestedBookRequestDTO Request DTO of the book that needs to be recommended.
     * @return {@link RequestedBookResponseDTO} of the newly saved requested book.
     * @throws BookAlreadyPresentException If a book with the given ISBN already exists in the database.
     */
    @Transactional
    @Override
    public RequestedBookResponseDTO saveRequestedBook(RequestedBookRequestDTO requestedBookRequestDTO) {
        String userEmail = requestedBookRequestDTO.userEmail();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(userEmail));
        Office office = user.getOffice();
        String officeName = office.getName();
        String bookIsbn = requestedBookRequestDTO.bookIsbn();
        Optional<Book> optionalBook = bookRepository.findByIsbnAndOfficeName(bookIsbn, officeName);
        if (optionalBook.isPresent()) {
            throw new BookAlreadyPresentException(bookIsbn, officeName);
        }

        //todo: implement method from book service that uses google books api to fetch given book & add the user to
        // the books liked_by

        return null;
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
     * @param requestedBookChangeStatusRequestDTO DTO for status change
     * @return {@link RequestedBookResponseDTO} of the updated requested book.
     * @throws RequestedBookNotFoundException If a requested book with the given ID does not exist.
     * @throws RequestedBookStatusException   If the status transition is not allowed.
     */
    @Transactional
    @Override
    public RequestedBookResponseDTO changeBookStatus(
            RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO) {
        BookStatus newBookStatus = requestedBookChangeStatusRequestDTO.newBookStatus();

        UUID requestedBookId = requestedBookChangeStatusRequestDTO.requestedBookId();
        RequestedBook requestedBook = requestedBookRepository.findById(requestedBookId)
                .orElseThrow(() -> new RequestedBookNotFoundException(requestedBookId));

        Book book = requestedBook.getBook();
        BookStatus currentBookStatus = book.getBookStatus();
        if (!bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus)) {
            throw new RequestedBookStatusException(currentBookStatus.name(), newBookStatus.name());
        }
        book.setBookStatus(newBookStatus);

        bookRepository.save(book);

        return requestedBookConverter.toRequestedBookResponseDTO(requestedBook);
    }

    /**
     * Adds or removes a like for a {@link RequestedBook} by a {@link User}.
     * <p>
     * This method updates the list of users who have liked the requested book and adjusts the like counter
     * accordingly.
     * </p>
     *
     * @param requestedBookRequestDTO Request DTO of the book that is liked/disliked.
     * @return {@link RequestedBookResponseDTO} with the updated like counter information.
     * @throws RequestedBookNotFoundException If a requested book with the given ID does not exist.
     * @throws UserNotFoundException          If a user with the given email does not exist.
     */
    @Transactional
    @Override
    public RequestedBookResponseDTO handleRequestedBookLike(RequestedBookRequestDTO requestedBookRequestDTO) {
        String email = requestedBookRequestDTO.userEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));

        Office office = user.getOffice();
        String officeName = office.getName();
        String isbn = requestedBookRequestDTO.bookIsbn();
        RequestedBook requestedBook = requestedBookRepository.findByIsbnAndOfficeName(isbn,
                officeName).orElseThrow(() -> new RequestedBookNotFoundException(isbn, officeName));

        Set<User> likedByUsers = requestedBook.getUsers();
        if (likedByUsers.contains(user)) {
            likedByUsers.remove(user);
        } else {
            likedByUsers.add(user);
        }
        requestedBook.refreshLikeCounter();
        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookResponseDTO(requestedBook);
    }
}