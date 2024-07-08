package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.projections.RequestedBookDTO;
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
 *  <h4><i>This class is used for implementing service logic for model RequestedBook.</i></h4>
 */
@Service
@RequiredArgsConstructor
public class RequestedBookServiceImpl implements RequestedBookService {

    private final RequestedBookRepository requestedBookRepository;
    private final RequestedBookConverter requestedBookConverter;

    private final static BookStatus requestedStatus = BookStatus.REQUESTED;
    private final static BookStatus pendingStatus = BookStatus.PENDING_PURCHASE;
    private final static BookStatus rejectedStatus = BookStatus.REJECTED;
    private final static BookStatus stockStatus = BookStatus.IN_STOCK;
    private final static List<BookStatus> validStatuses = new ArrayList<>(List.of(BookStatus.REQUESTED, BookStatus.PENDING_PURCHASE, BookStatus.REJECTED));

    /**
     * <b><i>Using this method, you can get all RecommendedBook objects without regard to the book's status.
     * (RECOMMENDED, PENDING_PURCHASE)</i></b>
     * <hr>
     *
     * @return (List of RecommendedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAll() {

        List<RequestedBook> requestedBooks = this.requestedBookRepository.findAll();

        return requestedBooks.stream()
                .map(requestedBookConverter::toRecommendedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * <b><i>Using this method, you can get all the RecommendedBook object with Book status = RECOMMENDED</i></b>
     * <hr>
     *
     * @return (List of RecommendedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAllRequestedBooks() {

        List<RequestedBook> requestedBooks = this.requestedBookRepository.findAllByBookStatus(requestedStatus);

        return requestedBooks.stream()
                .map(requestedBookConverter::toRecommendedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * <b><i>Using this method, you can get all the RecommendedBook object with Book status = PENDING_PURCHASE</i></b>
     * <hr>
     *
     * @return (List of RecommendedBookDTO)
     */
    @Override
    public List<RequestedBookDTO> getAllPendingRequestedBooks() {

        List<RequestedBook> requestedBooks = this.requestedBookRepository.findAllByBookStatus(pendingStatus);

        return requestedBooks.stream()
                .map(requestedBookConverter::toRecommendedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * <b><i>Using this method, you can get RecommendedBook by its id.</i></b>
     * <hr>
     *
     * @param id Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO).
     */
    @Override
    public RequestedBookDTO getRequestedBookById(UUID id) {

        RequestedBook requestedBook = this.getRequestedBook(id);

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    /**
     * <b><i>Using this method, you can get RecommendedBook by its isbn.</i></b>
     * <hr>
     *
     * @param isbn Type: <i><u>String</u></i>
     * @return (RecommendedBookDTO).
     */
    @Override
    public RequestedBookDTO getRequestedBookByISBN(String isbn) {

        Optional<RequestedBook> requestedBook = this.requestedBookRepository.findRequestedBookByTitle(isbn);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(isbn);
        }

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook.get());
    }

    /**
     * <b><i>Using this method, you can get RecommendedBook by its title.</i></b>
     * <hr>
     *
     * @param title Type: <i><u>String</u></i>
     * @return (RecommendedBookDTO).
     */
    @Override
    public RequestedBookDTO getRequestedBookByTitle(String title) {

        Optional<RequestedBook> requestedBook = this.requestedBookRepository.findRequestedBookByTitle(title);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(title);
        }

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook.get());
    }

    /**
     * <b><i>Using this method, you can get the favourite RecommendedBook among all RecommendedBooks that
     * are with status RECOMMENDED.</i></b>
     * <hr>
     * <p>The purpose of this is to enable us to obtain Recommended Books that are not in PENDING_PURCHASE
     * state for the purpose of adding them for purchasing.</p>
     * <b><i>Explanation:</i></b>
     * <p>Books with status PENDING_PURCHASE may have a larger like counter than all other RecommendedBooks,
     * therefore if we don't filter the books based on their RECOMMENDED status, it could happen that we only
     * ever retrieve books from PENDING_PURCHASE.</p>
     *
     * @return (RecommendedBookDTO).
     */
    @Override
    public RequestedBookDTO getFavoriteRequestedBook() {

        List<RequestedBook> requestedBook = this.requestedBookRepository.findTopByOrderByLikeCounterDesc(requestedStatus.toString());

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException();
        }

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook.getFirst());
    }

    /**
     * <b><i>Using this method, you can save RecommendedBook.</i></b>
     * <hr>
     * <p>Parameter bookISBN is used to insert new RecommendedBook from the <i>Google Book API</i></p>
     * <b>Constraints:</b>
     *  <ol>
     *      <li>Can't create RecommendedBook of Book that is already present in the database (<i>Book table</i>)</li>
     * </ol>
     *
     * @param bookISBN Type: <i><u>String</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that was saved.
     */
    @Override
    public RequestedBookDTO save(String bookISBN) {
        //todo: check if there is a Book with bookISBN

        //todo:implement using Google Books API

        return null;
    }

    /**
     * <b><i>Using this method, you can delete RecommendedBook.</i></b>
     * <hr>
     *
     * @param recommendedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we deleted.
     */
    @Override
    public RequestedBookDTO delete(UUID recommendedBookId) {

        Optional<RequestedBook> requestedBook = this.requestedBookRepository.findById(recommendedBookId);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(recommendedBookId);
        }

        this.requestedBookRepository.delete(requestedBook.get());

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook.get());
    }

    /**
     * <b><i>Using this method, you can set RecommendedBook status to PENDING_PURCHASE.</i></b>
     * <hr>
     * <b>Constraint:</b>
     * <ol>
     *     <li>RecommendedBook must exist.</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToPendingPurchase(UUID requestedBookId) {

        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();
        BookStatus methodStatus = BookStatus.PENDING_PURCHASE;

        if (bookStatus == methodStatus) {
            return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
        }

        if (!bookStatus.equals(requestedStatus) && !bookStatus.equals(rejectedStatus)) {
            throw new RequestedBookStatusException(bookStatus.name(), methodStatus.name());
        }

        requestedBook.getBook().setBookStatus(methodStatus);

        this.requestedBookRepository.save(requestedBook);

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    /**
     * <b><i>Using this method, you can set RecommendedBook status to RECOMMENDED.</i></b>
     * <hr>
     * <b>Constraints:</b>
     * <ol>
     *     <li>RecommendedBook must exist.</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToRequested(UUID requestedBookId) {

        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();
        BookStatus methodStatus = BookStatus.REQUESTED;

        if (bookStatus.equals(methodStatus)){
            return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
        }

        if (!bookStatus.equals(pendingStatus) && !bookStatus.equals(rejectedStatus)) {
            throw new RequestedBookStatusException(bookStatus.name(), methodStatus.name());
        }

        requestedBook.getBook().setBookStatus(methodStatus);

        this.requestedBookRepository.save(requestedBook);

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    /**
     * <b><i>Using this method, you can set RecommendedBook status to REJECTED.</i></b>
     * <hr>
     * <b>Constraints:</b>
     * <ol>
     *     <li>RecommendedBook must exist.</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToRejected(UUID requestedBookId) {

        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();
        BookStatus methodStatus = BookStatus.REJECTED;

        if (bookStatus.equals(methodStatus)){
            return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
        }

        if (!bookStatus.equals(pendingStatus) && !bookStatus.equals(requestedStatus)) {
            throw new RequestedBookStatusException(bookStatus.name(), methodStatus.name());
        }

        requestedBook.getBook().setBookStatus(methodStatus);

        this.requestedBookRepository.save(requestedBook);

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    /**
     * <b><i>Using this method, you can set RecommendedBook status to BookStatus to.</i></b>
     * <hr>
     * <b>Constraints:</b>
     * <ol>
     *     <li>RecommendedBook must exist.</li>
     *     <li>Conversions:</li>
     *     <ul>
     *         <li>You can switch between RECOMMENDED and REJECTED, and vice versa.</li>
     *         <li>You can switch between RECOMMENDED and PENDING_PURCHASE, and vice versa.</li>
     *         <li>You can switch between PENDING_PURCHASE and REJECTED, and vice versa.</li>
     *     </ul>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @param to Type: <i><u>BookStatus</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO changeStatus(UUID requestedBookId, BookStatus to) {

        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        BookStatus from = requestedBook.getBook().getBookStatus();

        if (from.equals(to)) {
            return requestedBookConverter.toRecommendedBookDTO(requestedBook);
        }

        if (!validStatuses.contains(from) || !validStatuses.contains(to)) {
            throw new RequestedBookStatusException(from.name(), to.name());
        }

        requestedBook.getBook().setBookStatus(to);

        this.requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    /**
     * <b><i>Using this method, you can set RecommendedBook status to IN_STOCK.</i></b>
     * <hr>
     * <b>Constraints:</b>
     * <ol>
     *     <li>RecommendedBook must exist.</li>
     *     <li>Conversions:</li>
     *     <ul>
     *         <li>You can only convert from PENDING_PURCHASE to IN_STOCK.</li>
     *     </ul>
     * </ol>
     *
     * <h1>NEED TO IMPLEMENT BOOKITEM INSERT</h1>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO buyRequestedBook(UUID requestedBookId) {

        //todo: now this method only changes status but in the feature it will need to implement adding BookItems;
        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        BookStatus from = requestedBook.getBook().getBookStatus();
        BookStatus to = stockStatus;

        if (from.equals(to)) {
            return requestedBookConverter.toRecommendedBookDTO(requestedBook);
        }

        if (!from.equals(pendingStatus)) {
            throw new RequestedBookStatusException(from.name(), to.name());
        }

        requestedBook.getBook().setBookStatus(to);

        this.requestedBookRepository.save(requestedBook);

        return requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    private RequestedBook getRequestedBook(UUID requestedBookId) {

        Optional<RequestedBook> requestedBook = this.requestedBookRepository.findById(requestedBookId);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        return requestedBook.get();
    }
}
