package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.dtos.BookItemDTO;

import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class BookItemConverter {

    public BookItemDTO toBookItemDTO(BookItem bookItem) {
        String isbn = bookItem.getBook().getIsbn();
        if (isbn == null) {
            throw new BookNotFoundException(isbn);
        }
        return new BookItemDTO(bookItem.getId(),
                isbn,
                bookItem.getBookItemState()
        );
    }

    public BookItem toBookItemEntity(BookItemDTO bookItemDTO, Book book) {
        BookItem bookItem = new BookItem();
        bookItem.setBook(book);
        bookItem.setBookItemState(bookItemDTO.bookItemState());
        return bookItem;
    }
}
