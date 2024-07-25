package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for querying {@link RequestedBook} data.
 * <p>
 * This service handles operations such as retrieving all requested books, retrieving a requested book by its ID,
 * retrieving requested books by their status, and retrieving requested books by their ISBN.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RequestedBookQueryServiceImpl implements RequestedBookQueryService {

    private final RequestedBookRepository requestedBookRepository;
    private final RequestedBookConverter requestedBookConverter;

    /**
     * Retrieves all entries for requested books in our system without considering their current status.
     * <hr>
     *
     * @return List of {@link RequestedBookDTO} representing all requested books.
     */
    @Override
    public List<RequestedBookDTO> getAllRequestedBooks() {
        List<RequestedBook> requestedBooks = requestedBookRepository.findAll();
        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .toList();
    }

    /**
     * Retrieves all {@link RequestedBook} objects with the given {@link BookStatus}.
     * <hr>
     *
     * @param status Type: {@link BookStatus} representing the book status.
     * @return List of {@link RequestedBookDTO} with the specified status.
     */
    @Override
    public List<RequestedBookDTO> getRequestedBooksByBookStatus(BookStatus status) {
        List<RequestedBook> requestedBooks = requestedBookRepository
                .findAllByBookBookStatusOrderByLikeCounterDescBookTitleAsc(status);
        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .toList();
    }

    /**
     * Retrieves a {@link RequestedBook} by its id.
     * <hr>
     *
     * @param requestedBookId Type: UUID representing the requested book ID.
     * @return {@link RequestedBookDTO} corresponding to the provided ID.
     * @throws RequestedBookNotFoundException if no requested book exists for the given UUID.
     */
    @Override
    public RequestedBookDTO getRequestedBookById(UUID requestedBookId) {
        RequestedBook requestedBook = requestedBookRepository.findById(requestedBookId)
                .orElseThrow(() -> new RequestedBookNotFoundException(requestedBookId));
        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Retrieves a {@link RequestedBook} by its ISBN.
     * <hr>
     *
     * @param isbn Type: String representing the book ISBN.
     * @return {@link RequestedBookDTO} corresponding to the provided ISBN.
     * @throws RequestedBookNotFoundException if no requested book exists for the given ISBN.
     */
    @Override
    public RequestedBookDTO getRequestedBookByISBN(String isbn) {
        RequestedBook requestedBook = requestedBookRepository.findByBookIsbn(isbn)
                .orElseThrow(() -> new RequestedBookNotFoundException(isbn));
        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }
}