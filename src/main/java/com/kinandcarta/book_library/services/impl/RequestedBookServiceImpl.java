package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.validators.BookStatusTransitionValidator;
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
import com.kinandcarta.book_library.services.RequestedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    private final BookStatusTransitionValidator bookStatusTransitionValidator;
    private final BookRepository bookRepository;

    /**
     * Retrieves all entries for requested books in our system without taking in consideration their current status.
     * <hr>
     *
     * @return List of {@link RequestedBookDTO}
     */
    @Override
    public List<RequestedBookDTO> getAllRequestedBooks() {

        List<RequestedBook> requestedBooks = requestedBookRepository.findAll();

        return requestedBooks.stream().map(requestedBookConverter::toRequestedBookDTO).toList();
    }

    /**
     * Retrieves all the RequestedBook object with given Book status, only changed by the admin and
     * an option to filter RequestedBook objects by title (enabled for everyone).
     * <hr>
     *
     * @param status Type: {@link BookStatus}
     * @return List of {@link RequestedBookDTO}
     */
    @Override
    public List<RequestedBookDTO> filterRequestedBooks(BookStatus status) {

        List<RequestedBook> requestedBooks;

        requestedBooks = requestedBookRepository
                .findAllByBookBookStatusOrderByLikeCounterDescBookTitleAsc(status);

        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .toList();
    }

    /**
     * Retrieves RequestedBook by its id.
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
     * Retrieves RequestedBook by its isbn.
     * <hr>
     *
     * @param isbn Type: String
     * @return {@link RequestedBookDTO}
     */
    @Override
    public RequestedBookDTO getRequestedBookByISBN(String isbn) {

        Optional<RequestedBook> requestedBookOptional = requestedBookRepository.findByBookIsbn(isbn);

        if (requestedBookOptional.isEmpty()) {
            throw new RequestedBookNotFoundException(isbn);
        }

        RequestedBook requestedBook = requestedBookOptional.get();

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Saves a RequestedBook object.
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

        Optional<Book> book = bookRepository.findByIsbn(bookISBN);

        if (book.isPresent()) {
            throw new BookAlreadyPresentException(bookISBN);
        }

        //todo:implement using Google Books API

        return null;
    }

    /**
     * Deletes a requested book by provided id.
     * <hr>
     *
     * @param requestedBookId Type: UUID
     * @return {@link RequestedBookDTO}
     */
    @Override
    public UUID deleteRequestedBookById(UUID requestedBookId) {

        boolean isRequestedBookPresent = requestedBookRepository.existsById(requestedBookId);

        if (!isRequestedBookPresent) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        requestedBookRepository.deleteById(requestedBookId);

        return requestedBookId;
    }

    /**
     * Changes the book status of a RequestedBook object with the provided ISBN.
     * <hr>
     * Constraints:
     * <ol>
     *     <li>RequestedBook must exist.</li>
     *     <li>Conversions:</li>
     *     <ul>
     *         <li>You can transition from status REQUESTED to PENDING_PURCHASE or REJECTED.</li>
     *         <li>You can transition from status REJECTED to PENDING_PURCHASE.</li>
     *         <li>You can transition from status PENDING_PURCHASE to REJECTED.</li>
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

        validateBookStatusTransition(currentBookStatus, newBookStatus);

        book.setBookStatus(newBookStatus);

        bookRepository.save(book);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Inserts a RequestedBook object in stock. After a book is added to stock,
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
     * @return {@link RequestedBookDTO}
     * @throws RequestedBookNotFoundException if RequestedBook object for given UUID doesn't exist
     * @throws RequestedBookStatusException   if the current Book status is not PENDING_PURCHASE
     */
    @Override
    public RequestedBookDTO enterRequestedBookInStock(UUID requestedBookId) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        Book book = requestedBook.getBook();

        BookStatus currentBookStatus = book.getBookStatus();
        BookStatus newBookStatus = BookStatus.IN_STOCK;

        validateBookStatusTransition(currentBookStatus, newBookStatus);

        book.setBookStatus(newBookStatus);

        bookRepository.save(book);

        //todo: call method for inserting BookItem objects

        requestedBookRepository.delete(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Adds and removes likes for RequestedBook object from given User object.
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
    public RequestedBookDTO handleRequestedBookLike(UUID requestedBookId, String userEmail) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        Optional<User> optionalUser = this.userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(userEmail);
        }

        User user = optionalUser.get();

        RequestedBook newVersionRequestedBook;

        Set<User> users = requestedBook.getUsers();

        if (users.contains(user)) {
            newVersionRequestedBook = removeLike(requestedBook, user);
        } else {
            newVersionRequestedBook = addLike(requestedBook, user);
        }

        requestedBookRepository.save(newVersionRequestedBook);

        return requestedBookConverter.toRequestedBookDTO(newVersionRequestedBook);
    }

    private void validateBookStatusTransition(BookStatus currentBookStatus, BookStatus newBookStatus) {

        if (!bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus)) {
            throw new RequestedBookStatusException(currentBookStatus.name(), newBookStatus.name());
        }
    }

    private RequestedBook addLike(RequestedBook requestedBook, User user) {

        requestedBook.increaseLikeCounter();

        Set<User> newLikedBy = requestedBook.getUsers();

        newLikedBy.add(user);

        requestedBook.addUsers(newLikedBy);

        return requestedBook;
    }

    private RequestedBook removeLike(RequestedBook requestedBook, User user) {

        requestedBook.decreaseLikeCounter();

        Set<User> newLikedBy = requestedBook.getUsers();

        newLikedBy.remove(user);

        requestedBook.addUsers(newLikedBy);

        return requestedBook;
    }

    private RequestedBook getRequestedBook(UUID requestedBookId) {

        Optional<RequestedBook> optionalRequestedBook = requestedBookRepository.findById(requestedBookId);

        if (optionalRequestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        return optionalRequestedBook.get();
    }
}
