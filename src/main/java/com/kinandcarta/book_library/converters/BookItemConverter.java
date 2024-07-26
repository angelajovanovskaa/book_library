package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.dtos.BookItemDTO;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * This class is responsible for providing conversion methods from {@link BookItem} entity to
 * Data Transfer Objects.
 */

@Component
public class BookItemConverter {
    /**
     * Converts a {@link BookItem} entity to a response DTO.
     *
     * @param bookItem The {@link BookItem} entity to convert.
     * @return a {@link BookDTO}
     */
    public BookItemDTO toBookItemDTO(BookItem bookItem) {
        Book book = bookItem.getBook();
        UUID id = bookItem.getId();
        String isbn = book.getIsbn();

        return new BookItemDTO(isbn, id);
    }
}
