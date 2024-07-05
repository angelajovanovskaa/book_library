package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RecommendedBookConverter;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RecommendedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RecommendedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.RecommendedBookStatusException;
import com.kinandcarta.book_library.projections.RecommendedBookDTO;
import com.kinandcarta.book_library.repositories.RecommendedBookRepository;
import com.kinandcarta.book_library.services.RecommendedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendedBookServiceImpl implements RecommendedBookService {

    private final RecommendedBookRepository recommendedBookRepository;
    private final RecommendedBookConverter recommendedBookConverter;
    private final GoogleBooksServiceImpl googleBooksService;
    private final static String recommendedFilter = String.valueOf(BookStatus.RECOMMENDED);
    private final static String pendingPurchaseFilter = String.valueOf(BookStatus.PENDING_PURCHASE);

    /**
     * <b><i>Using this method, you can get all RecommendedBook objects without regard to the book's status.
     * (RECOMMENDED, PENDING_PURCHASE)</i></b>
     * <hr>
     *
     * @return (List of RecommendedBookDTO)
     */
    @Override
    public List<RecommendedBookDTO> getAll() {
        List<RecommendedBook> recommendedBooks = this.recommendedBookRepository.findAll();

        return recommendedBooks.stream()
                .map(recommendedBookConverter::toRecommendedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * <b><i>Using this method, you can get all the RecommendedBook object with Book status = RECOMMENDED</i></b>
     * <hr>
     *
     * @return (List of RecommendedBookDTO)
     */
    @Override
    public List<RecommendedBookDTO> getAllRecommendedBooks() {
        List<RecommendedBook> recommendedBooks = this.recommendedBookRepository.findAllByBookStatus(BookStatus.RECOMMENDED);

        return recommendedBooks.stream()
                .map(recommendedBookConverter::toRecommendedBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * <b><i>Using this method, you can get all the RecommendedBook object with Book status = PENDING_PURCHASE</i></b>
     * <hr>
     *
     * @return (List of RecommendedBookDTO)
     */
    @Override
    public List<RecommendedBookDTO> getAllPendingRecommendedBooks() {
        List<RecommendedBook> recommendedBooks = this.recommendedBookRepository.findAllByBookStatus(BookStatus.PENDING_PURCHASE);

        return recommendedBooks.stream()
                .map(recommendedBookConverter::toRecommendedBookDTO)
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
    public RecommendedBookDTO getRecommendedBookById(UUID id) {
        Optional<RecommendedBook> recommendedBooks = this.recommendedBookRepository.findById(id);

        if (recommendedBooks.isEmpty()) {
            throw new RecommendedBookNotFoundException(id);
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBooks.get());
    }

    /**
     * <b><i>Using this method, you can get RecommendedBook by its isbn.</i></b>
     * <hr>
     *
     * @param isbn Type: <i><u>String</u></i>
     * @return (RecommendedBookDTO).
     */
    @Override
    public RecommendedBookDTO getRecommendedBookByISBN(String isbn) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findRecommendedBookByTitle(isbn);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(isbn);
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
    }

    /**
     * <b><i>Using this method, you can get RecommendedBook by its title.</i></b>
     * <hr>
     *
     * @param title Type: <i><u>String</u></i>
     * @return (RecommendedBookDTO).
     */
    @Override
    public RecommendedBookDTO getRecommendedBookByTitle(String title) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findRecommendedBookByTitle(title);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(title);
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
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
    public RecommendedBookDTO getFavoriteRecommendedBook() {
        List<RecommendedBook> recommendedBook = this.recommendedBookRepository.findTopByOrderByLikeCounterDesc(recommendedFilter);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException();
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.getFirst());
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
    public RecommendedBookDTO save(String bookISBN) {
        //todo: check if there is a Book with bookISBN

        //todo:implement using Google Books API
        String result = this.googleBooksService.searchBooks(bookISBN);

        Book object = this.googleBooksService.convertToBook(result);

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
    public RecommendedBookDTO delete(UUID recommendedBookId) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findById(recommendedBookId);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(recommendedBookId);
        }

        this.recommendedBookRepository.delete(recommendedBook.get());

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
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
     * @param recommendedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RecommendedBookDTO setStatusToPendingPurchase(UUID recommendedBookId) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findById(recommendedBookId);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(recommendedBookId);
        }

        String recommendedBookStatus = String.valueOf(recommendedBook.get().getBook().getBookStatus());
        if (!recommendedBookStatus.equals(recommendedFilter)) {
            throw new RecommendedBookStatusException(recommendedBookStatus, pendingPurchaseFilter);
        }

        recommendedBook.get().getBook().setBookStatus(BookStatus.PENDING_PURCHASE);

        this.recommendedBookRepository.save(recommendedBook.get());

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
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
     * @param recommendedBookId Type: <i><u>UUID</u></i>
     * @return (RecommendedBookDTO) The RecommendedBook object that we made changes on.
     */
    @Override
    public RecommendedBookDTO setStatusToRecommendedBook(UUID recommendedBookId) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findById(recommendedBookId);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(recommendedBookId);
        }

        String recommendedBookStatus = String.valueOf(recommendedBook.get().getBook().getBookStatus());
        if (!recommendedBookStatus.equals(pendingPurchaseFilter)) {
            throw new RecommendedBookStatusException(recommendedBookStatus, recommendedFilter);
        }

        recommendedBook.get().getBook().setBookStatus(BookStatus.RECOMMENDED);

        this.recommendedBookRepository.save(recommendedBook.get());

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
    }

}
