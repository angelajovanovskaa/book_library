package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
public class BookItemConverter {

    public BookItemDTO toBookItemDTO(BookItem bookItem) {
        Book book = bookItem.getBook();
        UUID id = bookItem.getId();
        String isbn = book.getIsbn();
        if (isbn == null) {
            throw new BookNotFoundException(isbn);
        }
        return new BookItemDTO(isbn, id);
    }
}
