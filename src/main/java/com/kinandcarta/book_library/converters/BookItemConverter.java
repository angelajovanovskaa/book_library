package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.projections.BookItemDTO;

import org.springframework.stereotype.Component;

@Component
public class BookItemConverter {

    public BookItemDTO toBookItemDTO(BookItem bookItem) {
        return BookItemDTO.builder().id(bookItem.getId()).ISBN(bookItem.getBook().getISBN()).bookItemState(bookItem.getBookItemState()).build();
    }

    public BookItem toBookItemEntity(BookItemDTO bookItemDTO, Book book) {
        BookItem bookItem = new BookItem();
        bookItem.setBook(book);
        bookItem.setBookItemState(bookItemDTO.bookItemState());
        return bookItem;
    }
}
