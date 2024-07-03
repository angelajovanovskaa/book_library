package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RecommendedBookConverter;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RecommendedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RecommendedBookNotFoundException;
import com.kinandcarta.book_library.projections.RecommendedBookDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
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

    @Override
    public List<RecommendedBookDTO> getAllRecommendedBooks() {
        List<RecommendedBook> recommendedBooks = this.recommendedBookRepository.findAll();

        return recommendedBooks.stream()
                .map(recommendedBookConverter::toRecommendedBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RecommendedBookDTO getRecommendedBookById(UUID id) {
        Optional<RecommendedBook> recommendedBooks = this.recommendedBookRepository.findById(id);

        if (recommendedBooks.isEmpty()) {
            throw new RecommendedBookNotFoundException(id);
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBooks.get());
    }

    @Override
    public RecommendedBookDTO getRecommendedBookByISBN(String isbn) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findByRecommendedBookByTitle(isbn);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(isbn);
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
    }

    @Override
    public RecommendedBookDTO getRecommendedBookByTitle(String title) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findByRecommendedBookByTitle(title);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(title);
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
    }

    @Override
    public RecommendedBookDTO getFavoriteRecommendedBook() {
        List<RecommendedBook> recommendedBook = this.recommendedBookRepository.findTopByOrderByLikeCounterDesc();

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException();
        }

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.getFirst());
    }

    @Override
    public RecommendedBookDTO save(String bookISBN) {
        //todo:implement using Google Books API
        String result = this.googleBooksService.searchBooks(bookISBN);

        Book object = this.googleBooksService.convertToBook(result);

        return null;
    }

    @Override
    public RecommendedBookDTO delete(UUID recommendedBookId) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findById(recommendedBookId);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(recommendedBookId);
        }

        this.recommendedBookRepository.delete(recommendedBook.get());

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
    }

    @Override
    public RecommendedBookDTO setStatusToPendingPurchase(UUID recommendedBookId) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findById(recommendedBookId);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(recommendedBookId);
        }

        recommendedBook.get().getBook().setBookStatus(BookStatus.PENDING_PURCHASE);

        this.recommendedBookRepository.save(recommendedBook.get());

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());    }

    @Override
    public RecommendedBookDTO setStatusToRejected(UUID recommendedBookId) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findById(recommendedBookId);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(recommendedBookId);
        }

        recommendedBook.get().getBook().setBookStatus(BookStatus.REJECTED);

        this.recommendedBookRepository.save(recommendedBook.get());

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());    }

    @Override
    public RecommendedBookDTO setStatusToRecommendedBook(UUID recommendedBookId) {
        Optional<RecommendedBook> recommendedBook = this.recommendedBookRepository.findById(recommendedBookId);

        if (recommendedBook.isEmpty()) {
            throw new RecommendedBookNotFoundException(recommendedBookId);
        }

        recommendedBook.get().getBook().setBookStatus(BookStatus.RECOMMENDED);

        this.recommendedBookRepository.save(recommendedBook.get());

        return this.recommendedBookConverter.toRecommendedBookDTO(recommendedBook.get());
    }

}
