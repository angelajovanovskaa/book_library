package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.projections.BookItemDTO;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.BookItemService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing book items in the library.
 */
@RequiredArgsConstructor
@Service
public class BookItemServiceImpl implements BookItemService {

    private final BookItemRepository bookItemRepository;
    private final BookRepository bookRepository;
    private final BookItemConverter bookItemConverter;

    /**
     * <b>Retrieves a list of BookItems associated with a specific book identified by ISBN.</b>
     *
     * @param isbn The ISBN of the book.
     * @return A list of BookItemDTOs representing BookItems associated with the book.
     */
    @Override
    public List<BookItemDTO> findByBookIsbn(String isbn) {
        List<BookItem> bookItems = bookItemRepository.findByBookIsbn(isbn);
        return bookItems.stream().map(bookItemConverter::toBookItemDTO).collect(Collectors.toList());
    }

    /**
     * Creates a new BookItem based on the provided BookItemDTO.
     *
     * @param bookItemDTO The DTO containing data for the new BookItem.
     * @return The newly created BookItem entity.
     * @throws BookNotFoundException If the BookItemDTO references a non-existent Book.
     */
    @Override
    public BookItem create(BookItemDTO bookItemDTO) {
        Optional<Book> maybeBook = bookRepository.findById(bookItemDTO.ISBN());
        if (maybeBook.isEmpty()) {
            throw new BookNotFoundException(maybeBook.get().getISBN());
        }
        BookItem bookItem = bookItemConverter.toBookItemEntity(bookItemDTO, maybeBook.get());

        return bookItemRepository.save(bookItem);
    }

    /**
     * Deletes a BookItem identified by its ID.
     *
     * @param id The ID of the BookItem to delete.
     * @return The ID of the deleted BookItem.
     * @throws BookItemNotFoundException If no BookItem exists with the given ID.
     */

    @Override
    public UUID deleteById(UUID id) {
        if (!bookItemRepository.existsById(id)) {
            throw new BookItemNotFoundException(id);
        }
        bookItemRepository.deleteById(id);
        return id;
    }
}
