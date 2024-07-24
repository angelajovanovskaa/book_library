package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class BookItemConverterTest {

    private final BookItemConverter bookItemConverter = new BookItemConverter();

    @Test
    void toBookItemDTO_conversionIsDone_returnsBookItemDTO() {
        //  given
        BookItem bookItem = getBookItem();
        BookItemDTO bookItemDTO = getBookItemDTO();

        //  when
        BookItemDTO result = bookItemConverter.toBookItemDTO(bookItem);

        //  then
        assertThat(result).isEqualTo(bookItemDTO);
    }


    private BookItem getBookItem() {
        Book book2 = new Book();
        book2.setIsbn("9780545414654");

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        List<BookItem> bookItems = new ArrayList<>();
        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        bookItems.add(bookItem1);

        book2.setBookItems(bookItems);

        return bookItem1;
    }

    private BookItemDTO getBookItemDTO() {
        Book book2 = new Book();
        book2.setIsbn("9780545414654");

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        return new BookItemDTO("9780545414654",
                UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215")
        );
    }
}