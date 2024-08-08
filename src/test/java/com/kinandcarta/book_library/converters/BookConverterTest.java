package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.BookTestData.getBook;
import static com.kinandcarta.book_library.utils.BookTestData.getBookDTO;
import static com.kinandcarta.book_library.utils.BookTestData.getBookDisplayDTO;
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
    void toBookEntity_conversionIsDone_returnsBookEntity() {
        // given
        Book book = getBook();
        BookDTO bookDTO = getBookDTO();
        Set<Author> authors = new HashSet<>();

        //  when
        Book result = bookConverter.toBookEntity(bookDTO, authors);

        //  then
        assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
        assertThat(result.getDescription()).isEqualTo(book.getDescription());
        assertThat(result.getLanguage()).isEqualTo(book.getLanguage());
        assertThat(result.getGenres()).isEqualTo(book.getGenres());
        assertThat(result.getTotalPages()).isEqualTo(book.getTotalPages());
        assertThat(result.getBookStatus()).isEqualTo(book.getBookStatus());
        assertThat(result.getImage()).isEqualTo(book.getImage());
        assertThat(result.getRatingFromWeb()).isEqualTo(book.getRatingFromWeb());
        assertThat(result.getRatingFromFirm()).isEqualTo(book.getRatingFromFirm());
        assertThat(result.getAuthors()).isEqualTo(book.getAuthors());
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