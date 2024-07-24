package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookConverterTest {

    private final BookConverter bookConverter = new BookConverter();

    @Test
    void toBookDTO_conversionIsDone_returnsBookDTO() {
        //  given
        Book book = getBook();
        BookDTO bookDTO = getBookDTO();

        //when
        BookDTO result = bookConverter.toBookDTO(book);

        //then
        assertThat(result).isEqualTo(bookDTO);
    }

    @Test
    void toBookEntity_conversionIsDone_returnsBookEntity() {
        //  given
        BookDTO bookDTO = getBookDTO();
        Book book = getBook();
        Set<Author> authors = book.getAuthors();

        //  when
        Book result = bookConverter.toBookEntity(bookDTO, authors);

        //  then
        assertThat(result).isEqualTo(book);
    }

    @Test
    void bookDisplayDTO_conversionIsDone_returnsToBookDisplay() {
        //  given
        Book book = getBook();
        BookDisplayDTO bookDisplayDTO = getToBookDisplayDTO();

        //  when
        BookDisplayDTO result = bookConverter.toBookDisplayDTO(book);

        //  then
        assertThat(result).isEqualTo(bookDisplayDTO);
    }


    private Book getBook() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        Book book1 = new Book();
        book1.setIsbn("765612382412");
        book1.setTitle("The Doors of Eden");
        book1.setDescription("book description");
        book1.setLanguage(Language.ENGLISH.toString());
        book1.setSummary("something");
        book1.setBookStatus(BookStatus.IN_STOCK);
        book1.setTotalPages(120);
        book1.setImage("https://google.com/images");
        book1.setRatingFromFirm(10.0);
        book1.setGenres(genres);

        Author author2 = new Author();
        author2.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author2.setFullName("Leah Thomas");

        book1.setAuthors(Set.of(author2));

        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book1);

        book1.setBookItems(List.of(bookItem1));

        return book1;
    }

    public BookDTO getBookDTO() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");

        return new BookDTO(
                "765612382412",
                "The Doors of Eden",
                "book description",
                Language.ENGLISH.toString(), genres,
                120, BookStatus.IN_STOCK,
                "https://google.com/images",
                0.0,
                10.0, Set.of(authorDTO1));
    }

    private BookDisplayDTO getToBookDisplayDTO() {

        return new BookDisplayDTO(
                "765612382412",
                "The Doors of Eden",
                Language.ENGLISH.toString(),
                "https://google.com/images");
    }
}