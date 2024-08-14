package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookItemConverterTest {

    private final BookItemConverter bookItemConverter = new BookItemConverter();

    @Test
    void toBookItemDTO_conversionIsDone_returnsBookItemDTO() {
        // given
        Book book = new Book();
        BookItem bookItem = new BookItem();

        book.setIsbn("9780545414654");
        bookItem.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem.setBook(book);
        bookItem.setBookItemState(BookItemState.AVAILABLE);

        BookItemDTO bookItemDTO = new BookItemDTO("9780545414654",
                UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));

        // when
        BookItemDTO result = bookItemConverter.toBookItemDTO(bookItem);

        // then
        assertThat(result).isEqualTo(bookItemDTO);
    }
}