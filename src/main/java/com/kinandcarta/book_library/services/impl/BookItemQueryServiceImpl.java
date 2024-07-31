package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookItemConverter;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.repositories.BookItemRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.BookItemQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link BookItemQueryService} that includes methods for retrieving various views of
 * book items.
 */
@Service
@RequiredArgsConstructor
public class BookItemQueryServiceImpl implements BookItemQueryService {

    private final BookItemRepository bookItemRepository;
    private final BookRepository bookRepository;
    private final BookItemConverter bookItemConverter;

    /**
     * Retrieves a list of BookItems associated with a specific book identified by ISBN.
     * Filtered by the office name.
     *
     * @param isbn The ISBN of the book.
     * @param officeName The name of the office that the book is located.
     * @return A list of BookItemDTOs representing BookItems associated with the book.
     */
    @Override
    public List<BookItemDTO> getBookItemsByBookIsbn(String isbn, String officeName) {
        List<BookItem> bookItems = bookItemRepository.findByBookIsbn(isbn, officeName);
        return bookItems.stream().map(bookItemConverter::toBookItemDTO).toList();
    }
}
