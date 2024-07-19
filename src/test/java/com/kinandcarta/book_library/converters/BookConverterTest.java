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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookConverterTest {

    private final BookConverter bookConverter= new BookConverter();

    @Test
    void toBookDTO_conversionIsDone_returnsBookDTO() {
        Book book = getBooks().getFirst();
        BookDTO bookDTO = getBookDTOs().getFirst();

        BookDTO result = bookConverter.toBookDTO(book);

        assertThat(result).isEqualTo(bookDTO);
    }

    @Test
    void toBookEntity_conversionIsDone_returnsBookEntity() {
        BookDTO bookDTO = getBookDTOs().getFirst();
        Book book = getBooks().getFirst();

        Set<Author> authors = book.getAuthors();

        Book result = bookConverter.toBookEntity(bookDTO, authors);

        assertThat(result).isEqualTo(book);
    }

    @Test
    void bookDisplayDTO_conversionIsDone_returnsBookDisplay() {
        Book book = getBooks().getFirst();
        BookDisplayDTO bookDisplayDTO = getBookDisplayDTOS().getFirst();

        BookDisplayDTO result = bookConverter.bookDisplayDTO(book);

        assertThat(result).isEqualTo(bookDisplayDTO);
    }


    private List<Book> getBooks() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        Book book1 = new Book();
        book1.setIsbn("765612382412");
        book1.setTitle("The Doors of Eden");
        book1.setDescription("book description");
        book1.setLanguage(Language.ENGLISH.toString());
        book1.setSummary("something");
        book1.setBookStatus(BookStatus.REQUESTED);
        book1.setTotalPages(120);
        book1.setImage("https://google.com/images");
        book1.setRatingFromFirm(10.0);
        book1.setGenres(genres);

        Author author2 = new Author();
        author2.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author2.setFullName("Leah Thomas");

        book1.setAuthors(Set.of(author2));


        Book book2 = new Book();
        book2.setIsbn("9780545414654");
        book2.setTitle("Mumbai of Us");
        book2.setDescription("book description");
        book2.setLanguage(Language.ENGLISH.toString());
        book2.setSummary("something");
        book2.setBookStatus(BookStatus.IN_STOCK);
        book2.setTotalPages(120);
        book2.setImage("https://google.com/images");
        book2.setRatingFromFirm(10.0);
        book2.setGenres(genres);

        Author author1 = new Author();
        author1.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author1.setFullName("Leah Thomas");

        book2.setAuthors(Set.of(author1));

        Book book3 = new Book();
        book3.setIsbn("143023240711654");
        book3.setTitle("Last of us");
        book3.setDescription("book description");
        book3.setLanguage(Language.ENGLISH.toString());
        book3.setSummary("something");
        book3.setBookStatus(BookStatus.IN_STOCK);
        book3.setTotalPages(120);
        book3.setImage("https://google.com/images");
        book3.setRatingFromFirm(10.0);

        book3.setGenres(genres);

        Author author3 = new Author();
        author3.setId(UUID.fromString("c909d9e4-9e3a-46e2-b2f9-3b7420a44023"));
        author3.setFullName("Valery Johnson");

        book3.setAuthors(Set.of(author3));

        List<BookItem> bookItems = new ArrayList<>();
        BookItem bookItem1 = new BookItem();
        bookItem1.setId(UUID.fromString("058edb04-38e7-43d8-991d-1df1cf829215"));
        bookItem1.setBookItemState(BookItemState.AVAILABLE);
        bookItem1.setBook(book2);

        BookItem bookItem2 = new BookItem();
        bookItem2.setId(UUID.fromString("07a1cbfb-3867-4b12-a0b5-46ad02387d11"));
        bookItem2.setBookItemState(BookItemState.BORROWED);
        bookItem2.setBook(book3);

        BookItem bookItem3 = new BookItem();
        bookItem3.setId(UUID.fromString("081284c7-54d3-4660-8974-0640d6f154ab"));
        bookItem3.setBookItemState(BookItemState.BORROWED);
        bookItem3.setBook(book3);

        bookItems.add(bookItem1);
        bookItems.add(bookItem2);
        bookItems.add(bookItem3);

        book2.setBookItems(bookItems);
        book3.setBookItems(bookItems);

        return List.of(book1, book2, book3);
    }

    public List<BookDTO> getBookDTOs() {
        String[] genres = {Genre.LANGUAGE_ARTS_DISCIPLINES.name(), Genre.TECHNOLOGY.name()};

        AuthorDTO authorDTO1 = new AuthorDTO("Leah Thomas");

        AuthorDTO authorDTO2 = new AuthorDTO("Valery Johnson");

        BookDTO bookDTO1 = new BookDTO(
                "765612382412",
                "The Doors of Eden",
                "book description",
                Language.ENGLISH.toString(), genres,
                120, BookStatus.REQUESTED,
                "https://google.com/images",
                0.0,
                10.0, Set.of(authorDTO1));

        BookDTO bookDTO2 = new BookDTO(
                "9780545414654",
                "Mumbai of Us",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.IN_STOCK,
                "https://google.com/images",
                0.0,
                10.0,
                Set.of(authorDTO1));

        BookDTO bookDTO3 = new BookDTO(
                "143023240711654",
                "Last of us",
                "book description",
                Language.ENGLISH.toString(),
                genres,
                120,
                BookStatus.IN_STOCK,
                "https://google.com/images",
                0.0,
                10.0,
                Set.of(authorDTO2));

        return List.of(bookDTO1, bookDTO2, bookDTO3);
    }

    private List<BookDisplayDTO> getBookDisplayDTOS() {
        BookDisplayDTO bookDisplayDTO1 = new BookDisplayDTO(
                "765612382412",
                "The Doors of Eden",
                Language.ENGLISH.toString(),
                "https://google.com/images");
        BookDisplayDTO bookDisplayDTO2 = new BookDisplayDTO(
                "9780545414654",
                "Mumbai of Us",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        BookDisplayDTO bookDisplayDTO3 = new BookDisplayDTO(
                "143023240711654",
                "Last of us",
                Language.ENGLISH.toString(),
                "https://google.com/images");

        return List.of(bookDisplayDTO1, bookDisplayDTO2, bookDisplayDTO3);
    }
}