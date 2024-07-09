package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.DTOs.RequestedBookDTO;
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
 *  <h4><i> Implementation of {@link RequestedBook} that contains service
 *  logic implementation of the CRUD operations for Review.</i></h4>
 */
@Service
@RequiredArgsConstructor
public class RequestedBookServiceImpl implements RequestedBookService {

    private final RequestedBookRepository requestedBookRepository;
    private final RequestedBookConverter requestedBookConverter;

    private final static List<BookStatus> validStatuses = new ArrayList<>(List.of(BookStatus.REQUESTED, BookStatus.PENDING_PURCHASE, BookStatus.REJECTED));

    /**
     * Using this method, you can get all RequestedBook objects without regard to the book's status.
     * (<i>REQUESTED, PENDING_PURCHASE</i>)
     * <hr>
     *
     * @return (List of RequestedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAll() {

        List<RequestedBook> requestedBooks = requestedBookRepository.findAll();

        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * Using this method, you can get all the RequestedBook object with Book status = <i>REQUESTED</i>
     * <hr>
     *
     * @return (List of RequestedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAllRequestedBooks() {

        List<RequestedBook> requestedBooks = requestedBookRepository.findAllByBookStatus(BookStatus.REQUESTED);

        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * Using this method, you can get all the RequestedBook object with Book status = <i>PENDING_PURCHASE</i>
     * <hr>
     *
     * @return (List of RequestedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAllPendingRequestedBooks() {

        List<RequestedBook> requestedBooks = requestedBookRepository.findAllByBookStatus(BookStatus.PENDING_PURCHASE);

        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * Using this method, you can get RequestedBook by its <i>id</i>.
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
     * Using this method, you can get RequestedBook by its <i>isbn</i>.
     * <hr>
     *
     * @param isbn Type: <i><u>String</u></i>
     * @return (RequestedBookDTO).
     */
    @Override
    public RequestedBookDTO getRequestedBookByISBN(String isbn) {

        Optional<RequestedBook> requestedBook = requestedBookRepository.findRequestedBookByTitle(isbn);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(isbn);
        }

        return requestedBookConverter.toRequestedBookDTO(requestedBook.get());
    }

    /**
     * Using this method, you can get RequestedBook by its <i>title</i>.
     * <hr>
     *
     * @param title Type: <i><u>String</u></i>
     * @return (RequestedBookDTO).
     */
    @Override
    public RequestedBookDTO getRequestedBookByTitle(String title) {

        Optional<RequestedBook> requestedBook = requestedBookRepository.findRequestedBookByTitle(title);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(title);
        }

        return requestedBookConverter.toRequestedBookDTO(requestedBook.get());
    }

    /**
     * Using this method, you can get the favourite RequestedBook among all RequestedBooks that
     * are with status <i>REQUESTED</i>.
     * <hr>
     * <p>The purpose of this is to enable us to obtain Requested Books that are not in <i>PENDING_PURCHASE</i>
     * state for the purpose of adding them for purchasing.</p>
     * <b><i>Explanation:</i></b>
     * <p>Books with status <i>PENDING_PURCHASE</i> may have a larger like counter than all other Requested Books,
     * therefore if we don't filter the books based on their <i>REQUESTED</i> status, it could happen that we only
     * ever retrieve books from <i>PENDING_PURCHASE</i>.</p>
     *
     * @return (RequestedBookDTO).
     */
    @Override
    public RequestedBookDTO getFavoriteRequestedBook() {

        List<RequestedBook> requestedBook = requestedBookRepository.findTopByOrderByLikeCounterDesc(BookStatus.REQUESTED.toString());

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException();
        }

        return requestedBookConverter.toRequestedBookDTO(requestedBook.getFirst());
    }

    /**
     * Using this method, you can save RequestedBook.
     * <hr>
     * <p>Parameter bookISBN is used to insert new RequestedBook from the <i>Google Book API</i></p>
     * <b><i>Constraints:</i></b>
     *  <ol>
     *      <li>Can't create RequestedBook of Book that is already present in the database (<i>Book table</i>)</li>
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

        requestedBookRepository.delete(requestedBook.get());

        return requestedBookConverter.toRequestedBookDTO(requestedBook.get());
    }

    /**
     * Using this method, you can set RequestedBook status to <i>PENDING_PURCHASE</i>.
     * <hr>
     * <b><i>Constraints:</i></b>
     * <ol>
     *     <li>RequestedBook must exist.</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToPendingPurchase(UUID requestedBookId) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();
        BookStatus methodStatus = BookStatus.PENDING_PURCHASE;

        if (bookStatus == methodStatus) {
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!bookStatus.equals(BookStatus.REQUESTED) && !bookStatus.equals(BookStatus.REJECTED)) {
            throw new RequestedBookStatusException(bookStatus.name(), methodStatus.name());
        }

        requestedBook.getBook().setBookStatus(methodStatus);

        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Using this method, you can set RequestedBook status to <i>REQUESTED</i>.
     * <hr>
     * <b><i>Constraints:</i></b>
     * <ol>
     *     <li>RequestedBook must exist.</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToRequested(UUID requestedBookId) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();
        BookStatus methodStatus = BookStatus.REQUESTED;

        if (bookStatus.equals(methodStatus)){
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!bookStatus.equals(BookStatus.PENDING_PURCHASE) && !bookStatus.equals(BookStatus.REJECTED)) {
            throw new RequestedBookStatusException(bookStatus.name(), methodStatus.name());
        }

        requestedBook.getBook().setBookStatus(methodStatus);

        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Using this method, you can set RequestedBook status to REJECTED.
     * <hr>
     * <b>Constraints:</b>
     * <ol>
     *     <li>RequestedBook must exist.</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToRejected(UUID requestedBookId) {

        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();
        BookStatus methodStatus = BookStatus.REJECTED;

        if (bookStatus.equals(methodStatus)){
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!bookStatus.equals(BookStatus.PENDING_PURCHASE) && !bookStatus.equals(BookStatus.REQUESTED)) {
            throw new RequestedBookStatusException(bookStatus.name(), methodStatus.name());
        }

        requestedBook.getBook().setBookStatus(methodStatus);

        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Using this method, you can set RequestedBook status to BookStatus to.
     * <hr>
     * <b><i>Constraints</i></b>:
     * <ol>
     *     <li>RequestedBook must exist.</li>
     *     <li>Conversions:</li>
     *     <ul>
     *         <li>You can switch between <i>REQUESTED</i> and <i>REJECTED</i>, and vice versa.</li>
     *         <li>You can switch between <i>REQUESTED</i> and <i>PENDING_PURCHASE</i>, and vice versa.</li>
     *         <li>You can switch between <i>PENDING_PURCHASE</i> and <i>REJECTED</i>, and vice versa.</li>
     *     </ul>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
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
     * <b><i>Using this method, you can set RequestedBook status to <i>IN_STOCK</i>.</i></b>
     * <hr>
     * <b><i>Constraints</i></b>:
     * <ol>
     *     <li>RequestedBook must exist.</li>
     *     <li>Conversions:</li>
     *     <ul>
     *         <li>You can only convert from <i>PENDING_PURCHASE</i> to <i>IN_STOCK</i>.</li>
     *     </ul>
     * </ol>
     *
     * <h1>NEED TO IMPLEMENT BOOKITEM INSERT</h1>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RequestedBookDTO) The RequestedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO buyRequestedBook(UUID requestedBookId) {

        //todo: now this method only changes status but in the feature it will need to implement adding BookItems;
        RequestedBook requestedBook = getRequestedBook(requestedBookId);

        BookStatus from = requestedBook.getBook().getBookStatus();
        BookStatus to = BookStatus.IN_STOCK;

        if (from.equals(to)) {
            return requestedBookConverter.toRequestedBookDTO(requestedBook);
        }

        if (!from.equals(BookStatus.PENDING_PURCHASE)) {
            throw new RequestedBookStatusException(from.name(), to.name());
        }

        requestedBook.getBook().setBookStatus(to);

        requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    private RequestedBook getRequestedBook(UUID requestedBookId) {

        Optional<RequestedBook> requestedBook = requestedBookRepository.findById(requestedBookId);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        return requestedBook.get();
    }
}
