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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestedBookServiceImpl implements RequestedBookService {

    private final RequestedBookRepository requestedBookRepository;
    private final RequestedBookConverter requestedBookConverter;

    private final static BookStatus requestedFilter = BookStatus.REQUESTED;
    private final static BookStatus pendingPurchaseFilter = BookStatus.PENDING_PURCHASE;
    private final static BookStatus rejectedFilter = BookStatus.REJECTED;

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

        List<RequestedBook> requestedBooks = this.requestedBookRepository.findAllByBookStatus(requestedFilter);

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

        List<RequestedBook> requestedBooks = this.requestedBookRepository.findAllByBookStatus(pendingPurchaseFilter);

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

        List<RequestedBook> requestedBook = this.requestedBookRepository.findTopByOrderByLikeCounterDesc(requestedFilter.toString());

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
     *     <li>You can only convert RecommendedBook status from RECOMMENDED to PENDING_PURCHASE
     *     (RecommendedBook status must be RECOMMENDED so the operation is successful).</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToPendingPurchase(UUID requestedBookId) {

        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        String bookStatus = requestedBook.getBook().getBookStatus().toString();

        if (!bookStatus.equals(requestedFilter) || !bookStatus.equals(rejectedFilter)) {
            throw new RequestedBookStatusException(bookStatus, pendingPurchaseFilter.name());
        }

        requestedBook.getBook().setBookStatus(pendingPurchaseFilter);

        this.requestedBookRepository.save(requestedBook);

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    /**
     * <b><i>Using this method, you can set RecommendedBook status to RECOMMENDED.</i></b>
     * <hr>
     * <b>Constraints:</b>
     * <ol>
     *     <li>RecommendedBook must exist.</li>
     *     <li>You can only convert RecommendedBook status from PENDING_PURCHASE to RECOMMENDED
     *     (RecommendedBook status must be PENDING_PURCHASE so the operation is successful).</li>
     * </ol>
     *
     * @param requestedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RequestedBookDTO setStatusToRequestedBook(UUID requestedBookId) {

        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();

        if (!bookStatus.equals(pendingPurchaseFilter) || !bookStatus.equals(rejectedFilter)) {
            throw new RequestedBookStatusException(bookStatus.name(), requestedFilter.name());
        }

        requestedBook.getBook().setBookStatus(requestedFilter);

        this.requestedBookRepository.save(requestedBook);

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    @Override
    public RequestedBookDTO setStatusToRejected(UUID requestedBookId) {

        RequestedBook requestedBook = this.getRequestedBook(requestedBookId);

        BookStatus bookStatus = requestedBook.getBook().getBookStatus();

        if (!bookStatus.equals(pendingPurchaseFilter) || !bookStatus.equals(requestedFilter)) {
            throw new RequestedBookStatusException(bookStatus.name(), rejectedFilter.name());
        }

        requestedBook.getBook().setBookStatus(rejectedFilter);

        this.requestedBookRepository.save(requestedBook);

        return this.requestedBookConverter.toRecommendedBookDTO(requestedBook);
    }

    private RequestedBook getRequestedBook(UUID requestedBookId) {

        Optional<RequestedBook> requestedBook = this.requestedBookRepository.findById(requestedBookId);

        if (requestedBook.isEmpty()) {
            throw new RequestedBookNotFoundException(requestedBookId);
        }

        return requestedBook.get();
    }
}
