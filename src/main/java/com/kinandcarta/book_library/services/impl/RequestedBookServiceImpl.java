package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.services.RequestedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RequestedBook} that contains service
 * logic implementation of the CRUD operations for RequestedBook.
 */
@Service
@RequiredArgsConstructor
public class RequestedBookServiceImpl implements RequestedBookService {

    private final RequestedBookRepository requestedBookRepository;
    private final RequestedBookConverter requestedBookConverter;

    private final static List<BookStatus> validStatuses = new ArrayList<>(List.of(BookStatus.REQUESTED, BookStatus.PENDING_PURCHASE, BookStatus.REJECTED));

    /**
     * Using this method, you can get all RequestedBook objects without regard to the book's status.
     * (REQUESTED, PENDING_PURCHASE)
     * <hr>
     *
     * @return (List of RequestedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAll() {

        List<RequestedBook> requestedBooks = requestedBookRepository.findAll();

        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .toList();
    }

    /**
     * Using this method, you can get all the RequestedBook object with given
     * Book status
     * <hr>
     *
     * @return (List of RequestedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAllRequestedBooksWithStatus(BookStatus status) {

        List<RequestedBook> requestedBooks = requestedBookRepository.findAllByBookBookStatus(status);

        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * Using this method, you can get RequestedBook by its id.
     * <hr>
     *
     * @param id Type: <i><u>UUID</u></i>
     * @return (RequestedBookDTO).
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
     * @param isbn Type: <i><u>String</u></i>
     * @return (RequestedBookDTO).
     */
    @Override
    public RequestedBookDTO getRequestedBookByISBN(String isbn) {

        Optional<RequestedBook> requestedBook = requestedBookRepository.findByBookISBN(isbn);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(isbn);
        }

        return requestedBookConverter.toRequestedBookDTO(requestedBook.get());
    }

    /**
     * Using this method, you can get RequestedBook by its title.
     * <hr>
     *
     * @param title Type: <i><u>String</u></i>
     * @return (RequestedBookDTO).
     */
    @Override
    public List<RequestedBookDTO> getRequestedBookByTitle(String title) {

        List<RequestedBookDTO> list = new ArrayList<>();

        Optional<RequestedBook> requestedBook = requestedBookRepository.findByBookTitle(title);

        requestedBook.ifPresent(book -> list.add(requestedBookConverter.toRequestedBookDTO(book)));

        return list;
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
     * @return (RequestedBookDTO).
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
     * @param bookISBN Type: <i><u>String</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that was saved.
     */
    @Override
    public RequestedBookDTO save(String bookISBN) {
        //todo: check if there is a Book with bookISBN

        //todo:implement using Google Books API

        return null;
    }

    /**
     * Using this method, you can delete RequestedBook.
     * <hr>
     * Delete RequestedBook object after insert of Book object (if RequestedBook object of that Book exists).
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that we deleted.
     */
    @Override
    public RequestedBookDTO deleteRequestedBook(UUID requestedBookId) {

        Optional<RequestedBook> requestedBook = requestedBookRepository.findById(requestedBookId);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        RequestedBookDTO requestedBookDTO = requestedBookConverter.toRequestedBookDTO(requestedBook.get());

        requestedBookRepository.delete(requestedBook.get());

        return requestedBookDTO;
    }

    /**
     * Using this method, you can set RequestedBook status to BookStatus to.
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
     * @param requestedBookId    Type: <i><u>UUID</u></i>
     * @param changeBookStatusTo Type: <i><u>BookStatus</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO changeStatus(UUID requestedBookId, BookStatus changeBookStatusTo) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        BookStatus defaultBookStatus = requestedBook.getBook().getBookStatus();

        if (defaultBookStatus.equals(changeBookStatusTo)) {
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!validStatuses.contains(defaultBookStatus) || !validStatuses.contains(changeBookStatusTo)) {
            throw new RequestedBookStatusException(defaultBookStatus.name(), changeBookStatusTo.name());
        }

        requestedBook.getBook().setBookStatus(changeBookStatusTo);

        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Using this method, you can set RequestedBook status to IN_STOCK
     * <p>
     * After admin decides to buy a certain book from the RequestedBook list with
     * Book objects with status REQUESTED this method is called. The RequestedBook
     * object status is set to IN_STOCK and the admin needs to input
     * number of BookItem object for that RequestedBook/Book object.
     * </p>
     * <hr>
     * Constraints:
     * <ol>
     *     <li>RequestedBook must exist.</li>
     *     <li>Conversions:</li>
     *     <ul>
     *         <li>You can only convert from PENDING_PURCHASE to IN_STOCK.</li>
     *     </ul>
     * </ol>
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
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO enterRequestedBookInStock(UUID requestedBookId) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        BookStatus defaultBookStatus = requestedBook.getBook().getBookStatus();
        BookStatus changeBookStatusTo = BookStatus.IN_STOCK;

        if (defaultBookStatus.equals(changeBookStatusTo)) {
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!defaultBookStatus.equals(BookStatus.PENDING_PURCHASE)) {
            throw new RequestedBookStatusException(defaultBookStatus.name(), changeBookStatusTo.name());
        }

        requestedBook.getBook().setBookStatus(changeBookStatusTo);

        //todo: call method for inserting BookItem objects

        RequestedBookDTO requestedBookDTO = requestedBookConverter.toRequestedBookDTO(requestedBook);

        //todo: call method for removing RequestedBook object

        return requestedBookDTO;
    }

    private RequestedBook getRequestedBook(UUID requestedBookId) {

        Optional<RequestedBook> requestedBook = requestedBookRepository.findById(requestedBookId);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        return requestedBook.get();
    }
}
