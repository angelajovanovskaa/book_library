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
        BookItem bookItem = getBookItems().getFirst();
        BookItemDTO bookItemDTO = getBookItemDTOs().getFirst();

        BookItemDTO result = bookItemConverter.toBookItemDTO(bookItem);

        assertThat(result).isEqualTo(bookItemDTO);
    }


    private List<BookItem> getBookItems() {
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

        BookItem bookItem2 = new BookItem();
        bookItem2.setId(UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));
        bookItem2.setBookItemState(BookItemState.BORROWED);
        bookItem2.setBook(book2);

        BookItem bookItem3 = new BookItem();
        bookItem3.setId(UUID.fromString("081284c7-54d3-4660-8974-0640d6f154ab"));
        bookItem3.setBookItemState(BookItemState.BORROWED);
        bookItem3.setBook(book2);

        bookItems.add(bookItem1);
        bookItems.add(bookItem2);
        bookItems.add(bookItem3);

        book2.setBookItems(bookItems);

        return List.of(bookItem1, bookItem2, bookItem3);
    }

    private List<BookItemDTO> getBookItemDTOs() {
        Book book2 = new Book();
        book2.setIsbn("9780545414654");


        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        List<BookItemDTO> bookItemDTOS = new ArrayList<>();

        BookItemDTO bookItemDTO1 = new BookItemDTO("9780545414654",
                UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215")
        );

        BookItemDTO bookItemDTO2 = new BookItemDTO("9780545414654",
                UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));


        BookItemDTO bookItemDTO3 = new BookItemDTO("9780545414654",
                UUID.fromString("081284c7-54d3-4660-8974-0640d6f154ab"));

        bookItemDTOS.add(bookItemDTO1);
        bookItemDTOS.add(bookItemDTO2);
        bookItemDTOS.add(bookItemDTO3);

        return List.of(bookItemDTO1, bookItemDTO2, bookItemDTO3);
    }
}