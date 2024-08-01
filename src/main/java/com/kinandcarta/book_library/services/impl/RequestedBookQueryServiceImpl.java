package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.OfficeNotFoundException;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for querying {@link RequestedBook} data.
 * <p>
 * This service handles operations such as retrieving all requested books, retrieving a requested book by its ID,
 * retrieving requested books by their status, and retrieving requested books by their ISBN.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class RequestedBookQueryServiceImpl implements RequestedBookQueryService {

    private final RequestedBookRepository requestedBookRepository;
    private final OfficeRepository officeRepository;
    private final RequestedBookConverter requestedBookConverter;

    /**
     * Retrieves all entries for requested books in our system for specified {@link Office} without considering their
     * current status.
     * <hr>
     *
     * @param officeName Representing the office name where the requested books are fetched from.
     * @return List of {@link RequestedBookResponseDTO} representing all requested books.
     */
    @Override
    public List<RequestedBookResponseDTO> getAllRequestedBooksByOfficeName(String officeName) {
        Optional<Office> office = officeRepository.findById(officeName);
        if (office.isEmpty()) {
            throw new OfficeNotFoundException(officeName);
        }

        List<RequestedBook> requestedBooks = requestedBookRepository.findAllByBookOfficeName(officeName);
        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .toList();
    }

    /**
     * Retrieves all {@link RequestedBook} objects for {@link Office} with the given {@link BookStatus}.
     * <hr>
     *
     * @param status     {@link BookStatus} representing the book status.
     * @param officeName Representing the office name where the requested books are fetched from.
     * @return List of {@link RequestedBookResponseDTO} with the specified status for the specified office.
     */
    @Override
    public List<RequestedBookResponseDTO> getRequestedBooksByBookStatusAndOfficeName(BookStatus status,
                                                                                     String officeName) {
        List<RequestedBook> requestedBooks = requestedBookRepository
                .findAllByBookBookStatusAndBookOfficeNameOrderByLikeCounterDescBookTitleAsc(status, officeName);
        return requestedBooks.stream()
                .map(requestedBookConverter::toRequestedBookDTO)
                .toList();
    }

    /**
     * Retrieves a {@link RequestedBook} by its id.
     * <hr>
     *
     * @param requestedBookId Type: UUID representing the requested book ID.
     * @return {@link RequestedBookResponseDTO} corresponding to the provided ID.
     * @throws RequestedBookNotFoundException if no requested book exists for the given UUID.
     */
    @Override
    public RequestedBookResponseDTO getRequestedBookById(UUID requestedBookId) {
        RequestedBook requestedBook = requestedBookRepository.findById(requestedBookId)
                .orElseThrow(() -> new RequestedBookNotFoundException(requestedBookId));
        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }

    /**
     * Retrieves a {@link RequestedBook} by its ISBN and {@link Office} name.
     * <hr>
     *
     * @param isbn       Representing the book ISBN.
     * @param officeName Representing the office name where the requested book is fetched from.
     * @return {@link RequestedBookResponseDTO} corresponding result to the provided ISBN and office name.
     * @throws RequestedBookNotFoundException if no requested book exists for the given ISBN and office name.
     */
    @Override
    public RequestedBookResponseDTO getRequestedBookByISBNAndOfficeName(String isbn, String officeName) {
        RequestedBook requestedBook = requestedBookRepository.findByBookIsbnAndBookOfficeName(isbn, officeName)
                .orElseThrow(() -> new RequestedBookNotFoundException(isbn, officeName));
        return requestedBookConverter.toRequestedBookDTO(requestedBook);
    }
}