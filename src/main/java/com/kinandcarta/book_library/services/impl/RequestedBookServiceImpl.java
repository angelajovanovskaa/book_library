package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.RequestedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of {@link RequestedBook} that contains service
 * logic implementation of the CRUD operations for RequestedBook.
 */
@Service
@RequiredArgsConstructor
public class RequestedBookServiceImpl implements RequestedBookService {

    private final RequestedBookRepository requestedBookRepository;
    private final RequestedBookConverter requestedBookConverter;

    private final UserRepository userRepository;

    /**
     * Retrieves all entries for requested books in our system without taking in consideration their current status.
     * <hr>
     *
     * @return List of {@link RequestedBookDTO}
     */
    @Override
    public List<RequestedBookDTO> getAll() {

        List<RequestedBook> requestedBooks = requestedBookRepository.findAll();

        return requestedBooks.stream().map(requestedBookConverter::toRequestedBookDTO).toList();
    }

    /**
     * Using this method, you can get all the RequestedBook object with given
     * Book status
     * <hr>
     *
     * @return List of {@link RequestedBookDTO}
     */
    @Override
    public List<RequestedBookDTO> getAllRequestedBooksWithStatus(BookStatus status) {

        BookStatus defaultStatus = BookStatus.REQUESTED;

        BookStatus filterStatus = Objects.requireNonNullElse(status, defaultStatus);

        List<RequestedBook> requestedBooks = requestedBookRepository.findAllByBookBookStatus(filterStatus);

        return requestedBooks.stream().map(requestedBookConverter::toRequestedBookDTO).toList();
    }

    /**
     * Using this method, you can get all the RequestedBook object with given
     * Book status, only changed by the admin and filter by isbn or title depending on the
     * user choice from the front end.
     * <hr>
     * Type can be title or isbn (drop down list with 2 options title and isbn), input is the value
     * that the user enters and status is a dropdown list only visible to the admin, users have
     * predefined REQUESTED status.A
     *
     * @return List of {@link RequestedBookDTO}
     */
    @Override
    public List<RequestedBookDTO> filterRequestedBooks(String type, String input, BookStatus status) {

        BookStatus defaultStatus = BookStatus.REQUESTED;

        BookStatus filterStatus = Objects.requireNonNullElse(status, defaultStatus);

        List<RequestedBook> requestedBooks = requestedBookRepository.findAllByBookBookStatus(filterStatus);

        if (input == null || input.isEmpty()) {
            return requestedBooks.stream().map(requestedBookConverter::toRequestedBookDTO).toList();
        }

        if (type.equals("title")) {
            requestedBooks = requestedBookRepository.findAllByBookBookStatusAndBookTitleContainingIgnoreCase(filterStatus, input);
        } else if (type.equals("isbn")) {
            requestedBooks = requestedBookRepository.findAllByBookBookStatusAndBookISBNContainingIgnoreCase(filterStatus, input);
        }

        return requestedBooks.stream().map(requestedBookConverter::toRequestedBookDTO).toList();
    }

    /**
     * Using this method, you can get RequestedBook by its id.
     * <hr>
     *
     * @param id Type: UUID
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO getRequestedBookById(UUID id) {

        RequestedBook requestedBook = getRequestedBook(id);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Using this method, you can get RequestedBook by its isbn.
     * <hr>
     *
     * @param isbn Type: String
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO getRequestedBookByISBN(String isbn) {

        Optional<RequestedBook> optionalBook = requestedBookRepository.findByBookISBN(isbn);

        if (optionalBook.isEmpty()) {
            throw new RequestedBookNotFoundException(isbn);
        }

        RequestedBook requestedBook = optionalBook.get();

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }


    /**
     * Using this method, you can get the favourite RequestedBook among all RequestedBooks that
     * are with status REQUESTED.
     * <hr>
     * <p>
     * The purpose of this is to enable us to obtain Requested Books that are not in PENDING_PURCHASE
     * state for the purpose of adding them for purchasing.
     * </p>
     * Explanation:
     * <p>
     * Books with status PENDING_PURCHASE may have a larger like counter than all other Requested Books,
     * therefore if we don't filter the books based on their REQUESTED status, it could happen that we only
     * ever retrieve books from PENDING_PURCHASE.
     * </p>
     *
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO getFavoriteRequestedBook() {

        Optional<RequestedBook> requestedBook = requestedBookRepository.findTopByBookBookStatusOrderByLikeCounterDescBookTitleAsc(BookStatus.REQUESTED);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException();
        }

        return requestedBookConverter.toRequestedBookDTO(requestedBook.get());
    }

    /**
     * Using this method, you can save RequestedBook.
     * <hr>
     * <p>Parameter bookISBN is used to insert new RequestedBook from the <i>Google Book API</i></p>
     * Constraints:
     *  <ol>
     *      <li>Can't create RequestedBook of Book that is already present in the database (Book table)</li>
     * </ol>
     *
     * @param bookISBN Type: String
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO saveRequestedBook(String bookISBN) {
        //todo: check if there is a Book with bookISBN

        //todo:implement using Google Books API

        return null;
    }

    /**
     * Using this method, you can delete RequestedBook.
     * <hr>
     * Delete RequestedBook object after insert of Book object (if RequestedBook object of that Book exists).
     *
     * @param requestedBookId Type: UUID
     * @return {@link RequestedBookDTO}
     */
    @Override
    public UUID deleteRequestedBook(UUID requestedBookId) {

        if (!requestedBookRepository.existsById(requestedBookId)) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        requestedBookRepository.deleteById(requestedBookId);

        return requestedBookId;
    }

    /**
     * Using this method, you can the book status of a RequestedBook object with the provided ISBN.
     * <hr>
     * Constraints:
     * <ol>
     *     <li>RequestedBook must exist.</li>
     *     <li>Conversions:</li>
     *     <ul>
     *         <li>You can switch between REQUESTED and REJECTED, and vice versa.</li>
     *         <li>You can switch between REQUESTED and PENDING_PURCHASE, and vice versa.</li>
     *         <li>You can switch between PENDING_PURCHASE and REJECTED, and vice versa.</li>
     *     </ul>
     * </ol>
     *
     * @param requestedBookId Type: UUID
     * @param newBookStatus   Type: BookStatus
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO changeBookStatus(UUID requestedBookId, BookStatus newBookStatus) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        Book book = requestedBook.getBook();

        BookStatus currentBookStatus = book.getBookStatus();

        if (currentBookStatus == newBookStatus) {
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!validateBookStatusTransition(currentBookStatus, newBookStatus)) {
            throw new RequestedBookStatusException(currentBookStatus.name(), newBookStatus.name());
        }

        book.setBookStatus(newBookStatus);

        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Using this method, you can enter a RequestedBook object in stock. After a book is added to stock,
     * the corresponding requested book is removed.
     * <hr>
     *
     * <p>NEED TO IMPLEMENT BOOKITEM INSERT</p>
     * <p>FLOW</p>
     * <ol>
     *     <li>Admin calls this method.</li>
     *     <li>Book status goes from PENDING_PURCHASE to IN_STOCK.</li>
     *     <li>Admin needs to enter number of copies of the Book object.</li>
     *     <li>RequestedBook object is deleted from the table.</li>
     * </ol>
     *
     * @param requestedBookId Type: UUID
     * @throws RequestedBookNotFoundException if RequestedBook object for given UUID doesn't exist
     * @throws RequestedBookStatusException if the current Book status is not PENDING_PURCHASE
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO enterRequestedBookInStock(UUID requestedBookId) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        BookStatus currentBookStatus = requestedBook.getBook().getBookStatus();
        BookStatus newBookStatus = BookStatus.IN_STOCK;

        if (currentBookStatus.equals(newBookStatus)) {
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!currentBookStatus.equals(BookStatus.PENDING_PURCHASE)) {
            throw new RequestedBookStatusException(currentBookStatus.name(), newBookStatus.name());
        }

        requestedBook.getBook().setBookStatus(newBookStatus);

        //todo: call method for inserting BookItem objects

        RequestedBookDTO requestedBookDTO = requestedBookConverter.toRequestedBookDTO(requestedBook);

        //todo: call method for removing RequestedBook object

        return requestedBookDTO;
    }

    /**
     * Using this method, you ADD and REMOVE likes from RequestedBook object.
     * <hr>
     * <p>
     * Everything is implemented in a single method. It uses the List of User objects
     * in RequestedBook to check if a User object already liked the RequestedBook
     * or not and depending on that it adds/removes the User object from the List and
     * changes the likeCounter.
     * </p>
     *
     * @param requestedBookId Type: UUID
     * @param userEmail       Type: String
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO likeRequestedBook(UUID requestedBookId, String userEmail) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        User user = getUser(userEmail);

        RequestedBook newVersionRequestedBook;
        if (requestedBook.getUsers().contains(user)) {
            newVersionRequestedBook = removeLike(requestedBook, user);
        } else {
            newVersionRequestedBook = addLike(requestedBook, user);
        }

        requestedBookRepository.save(newVersionRequestedBook);

        return requestedBookConverter.toRequestedBookDTO(newVersionRequestedBook);
    }

    private boolean validateBookStatusTransition(BookStatus currentBookStatus, BookStatus newBookStatus) {
        return (currentBookStatus == BookStatus.REQUESTED && (newBookStatus == BookStatus.REJECTED || newBookStatus == BookStatus.PENDING_PURCHASE)) || (currentBookStatus == BookStatus.REJECTED && newBookStatus == BookStatus.PENDING_PURCHASE) || (currentBookStatus == BookStatus.PENDING_PURCHASE && newBookStatus == BookStatus.REJECTED);
    }

    private RequestedBook addLike(RequestedBook requestedBook, User user) {

        requestedBook.increaseLikeCounter();

        Set<User> likedBy = requestedBook.getUsers();

        likedBy.add(user);

        return requestedBook;
    }

    private RequestedBook removeLike(RequestedBook requestedBook, User user) {

        requestedBook.decreaseLikeCounter();

        Set<User> likedBy = requestedBook.getUsers();

        likedBy.remove(user);

        return requestedBook;
    }

    private RequestedBook getRequestedBook(UUID requestedBookId) {

        Optional<RequestedBook> optionalRequestedBook = requestedBookRepository.findById(requestedBookId);

        if (optionalRequestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        return optionalRequestedBook.get();
    }

    private User getUser(String userEmail) {

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(userEmail);
        }

        return optionalUser.get();
    }
}
