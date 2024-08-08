package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.utils.BookItemResponseMessages;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link BookItemManagementService} that includes methods for retrieving various operations of
 * book items.
 */
@RequiredArgsConstructor
@Service
public class BookItemManagementServiceImpl implements BookItemManagementService {

    private final BookItemRepository bookItemRepository;
    private final BookRepository bookRepository;
    private final BookItemConverter bookItemConverter;

    /**
     * Inserts a new book item with the state set to AVAILABLE for the specified book isbn.
     *
     * @param isbn       The isbn of the book for which a new item is to be inserted.
     * @param officeName The name of the office that the book is located.
     * @return A DTO representation of the newly inserted book item.
     * @throws BookNotFoundException If no book with the specified isbn is found.
     */
    @Override
    public BookItemDTO insertBookItem(String isbn, String officeName) {
        Optional<Book> bookOptional = bookRepository.findByIsbnAndOfficeName(isbn, officeName);
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException(isbn);
        }
        BookItem bookItem = new BookItem();
        bookItem.setBookItemState(BookItemState.AVAILABLE);
        Book book = bookOptional.get();
        bookItem.setBook(book);
        BookItem savedBookItem = bookItemRepository.save(bookItem);

        return bookItemConverter.toBookItemDTO(savedBookItem);
    }

    /**
     * Deletes a BookItem identified by its id.
     *
     * @param id The id of the BookItem to delete.
     * @return The id of the deleted BookItem.
     * @throws BookItemNotFoundException If no BookItem exists with the given id.
     */
    @Override
    public UUID deleteById(UUID id) {
        BookItem bookItem = bookItemRepository.findById(id)
                .orElseThrow(() -> new BookItemNotFoundException(id));

        bookItemRepository.deleteById(bookItem.getId());
        return id;
    }

    /**
     * This method is used to report a book as damaged after a return.
     * All users will have access to this method. Only accessible after a successful return
     *
     * @param bookItemId UUID value for the id of the BookItem, cannot be {@code null}
     * @return A message indicating that the book has been reported as damaged.
     * @throws BookItemNotFoundException if bookItemId is {@code null}
     */
    @Override
    public String reportBookItemAsDamaged(UUID bookItemId) {
        BookItem bookItem = bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        bookItem.setBookItemState(BookItemState.DAMAGED);
        bookItemRepository.save(bookItem);

        return BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED;
    }

    /**
     * This method is used to report a book as lost.
     * All users will have access to this method.
     *
     * @param bookItemId UUID value for the id of the BookItem, cannot be {@code null}
     * @return A message indicating that the book has been reported as lost.
     * @throws BookItemNotFoundException if bookItemId is {@code null}
     */
    @Override
    public String reportBookItemAsLost(UUID bookItemId) {
        BookItem bookItem = bookItemRepository.findById(bookItemId)
                .orElseThrow(() -> new BookItemNotFoundException(bookItemId));

        bookItem.setBookItemState(BookItemState.LOST);
        bookItemRepository.save(bookItem);

        return BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST;
    }
}