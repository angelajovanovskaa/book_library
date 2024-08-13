package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.ReviewTestData.getReviewResponseDTOs;
import static com.kinandcarta.book_library.utils.SharedTestData.SKOPJE_OFFICE;
import static com.kinandcarta.book_library.utils.SharedTestData.SOFIJA_OFFICE;

@UtilityClass
public class BookTestData {
    public static final String BOOK_ISBN = "isbn1";
    public static final String BOOK_TITLE = "title1";
    public static final String BOOK_TITLE_SEARCH_TERM = "ti";
    public static final String BOOK_DESCRIPTION = "description";
    public static final String BOOK_SUMMARY = "summary";
    public static final int BOOK_TOTAL_PAGES = 1;
    public static final String BOOK_LANGUAGE = "MK";
    public static final Double BOOK_RATING_FROM_WEB = 1.0;
    public static final Double BOOK_RATING_FROM_FIRM = 1.0;
    public static final String BOOK_IMAGE = "image1";
    public static final BookStatus BOOK_STATUS = BookStatus.REQUESTED;
    public static final String[] BOOK_GENRES = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};
    public static final AuthorDTO AUTHOR_DTO = new AuthorDTO("author1");
    public static final Set<AuthorDTO> BOOK_AUTHORS = new HashSet<>(Collections.singleton(AUTHOR_DTO));

    public static List<Book> getBooks() {
        Author author = new Author();
        author.setId(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507061"));
        author.setFullName("Leah Thomas");
        HashSet<Author> authors = new HashSet<>();
        authors.add(author);
        List<Book> books = new ArrayList<>();
        Book book1 = new Book(
                BOOK_ISBN,
                SKOPJE_OFFICE,
                BOOK_TITLE,
                BOOK_DESCRIPTION,
                BOOK_SUMMARY,
                BOOK_TOTAL_PAGES,
                BOOK_LANGUAGE,
                BOOK_RATING_FROM_WEB,
                BOOK_RATING_FROM_FIRM,
                BOOK_IMAGE,
                BOOK_STATUS,
                BOOK_GENRES,
                authors,
                new ArrayList<>()
        );
        books.add(book1);
        Book book2 = new Book(
                "isbn2",
                SKOPJE_OFFICE,
                "title2",
                BOOK_DESCRIPTION,
                BOOK_SUMMARY,
                BOOK_TOTAL_PAGES,
                BOOK_LANGUAGE,
                BOOK_RATING_FROM_WEB,
                BOOK_RATING_FROM_FIRM,
                BOOK_IMAGE,
                BOOK_STATUS,
                BOOK_GENRES,
                authors,
                new ArrayList<>()
        );
        books.add(book2);

        return books;
    }

    public static Book getBook() {
        return getBooks().getFirst();
    }

    public static Book getLastBook() {
        return getBooks().getLast();
    }

    public static Book getBookFromDifferentOffice() {
        return new Book(
                "isbn3",
                SOFIJA_OFFICE,
                "title3",
                BOOK_DESCRIPTION,
                BOOK_SUMMARY,
                BOOK_TOTAL_PAGES,
                BOOK_LANGUAGE,
                BOOK_RATING_FROM_WEB,
                BOOK_RATING_FROM_FIRM,
                BOOK_IMAGE,
                BOOK_STATUS,
                BOOK_GENRES,
                new HashSet<>(),
                new ArrayList<>()
        );
    }

    public static List<BookDetailsDTO> getBookDTOs() {
        BookDetailsDTO bookDTO1 = new BookDetailsDTO(
                BOOK_ISBN,
                "title1",
                BOOK_DESCRIPTION,
                BOOK_LANGUAGE,
                BOOK_GENRES,
                BOOK_TOTAL_PAGES,
                BOOK_STATUS,
                BOOK_IMAGE,
                BOOK_RATING_FROM_WEB,
                BOOK_RATING_FROM_FIRM,
                new HashSet<>(),
                SKOPJE_OFFICE.getName(),
                getReviewResponseDTOs()
        );
        BookDetailsDTO bookDTO2 = new BookDetailsDTO(
                "isbn2",
                "title2",
                BOOK_DESCRIPTION,
                BOOK_LANGUAGE,
                BOOK_GENRES,
                BOOK_TOTAL_PAGES,
                BOOK_STATUS,
                BOOK_IMAGE,
                BOOK_RATING_FROM_WEB,
                BOOK_RATING_FROM_FIRM,
                new HashSet<>(),
                SKOPJE_OFFICE.getName(),
                getReviewResponseDTOs()
        );

        return List.of(bookDTO1, bookDTO2);
    }

    public static BookDetailsDTO getBookDTO() {
        return getBookDTOs().getFirst();
    }

    public static List<BookDisplayDTO> getBookDisplayDTOs() {
        BookDisplayDTO bookDisplayDTO1 = new BookDisplayDTO(
                BOOK_ISBN,
                "title1",
                BOOK_LANGUAGE,
                BOOK_IMAGE
        );

        BookDisplayDTO bookDisplayDTO2 = new BookDisplayDTO(
                "isbn2",
                "title2",
                BOOK_LANGUAGE,
                BOOK_IMAGE
        );

        return List.of(bookDisplayDTO1, bookDisplayDTO2);
    }

    public static BookDisplayDTO getBookDisplayDTO() {
        return getBookDisplayDTOs().getFirst();
    }

    public BookInsertRequestDTO getBookInsertRequestDTOgetBookInsertRequestDTO() {
        return new BookInsertRequestDTO(
                BOOK_ISBN,
                BOOK_TITLE,
                BOOK_DESCRIPTION,
                BOOK_LANGUAGE,
                BOOK_GENRES,
                BOOK_TOTAL_PAGES,
                BOOK_IMAGE,
                BOOK_RATING_FROM_WEB,
                BOOK_AUTHORS,
                SKOPJE_OFFICE.getName()
        );
    }
}
