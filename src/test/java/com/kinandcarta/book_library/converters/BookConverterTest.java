package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.BookTestData.*;
import static com.kinandcarta.book_library.utils.OfficeTestData.OFFICE;
import static org.assertj.core.api.Assertions.assertThat;

class BookConverterTest {

    private final BookConverter bookConverter = new BookConverter();

    @Test
    void toBookDTO_conversionIsDone_returnsBookDTO() {
        //  given
        Book book = getBook();
        BookDTO bookDTO = getBookDTO();

        //  when
        BookDTO result = bookConverter.toBookDTO(book);

        //  then
        assertThat(result).isEqualTo(bookDTO);
    }

    @Test
    void toBookEntity_conversionIsDone_returnsBookEntity1() {
        // given
        Book book = getBook();
        BookDTO bookDTO = getBookDTO();
        Set<Author> authors = new HashSet<>();

        //  when
        Book result = bookConverter.toBookEntity(bookDTO, authors);
        result.setOffice(OFFICE);

        //  then
        assertThat(result).isEqualTo(book);
    }

    @Test
    void bookDisplayDTO_conversionIsDone_returnsToBookDisplay() {
        //  given
        Book book = getBook();
        BookDisplayDTO bookDisplayDTO = getBookDisplayDTO();

        //  when
        BookDisplayDTO result = bookConverter.toBookDisplayDTO(book);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTO);
    }
}