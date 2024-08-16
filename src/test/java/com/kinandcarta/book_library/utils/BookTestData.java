package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.AuthorDTO;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BookTestData {
    public static final String BOOK_ISBN = "isbn1";
    public static final String BOOK_INVALID_ISBN = "invalidISBN";
    public static final BookId BOOK_ID = new BookId(BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE.getName());
    public static final BookIdDTO BOOK_ID_DTO = new BookIdDTO(BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE.getName());
    public static final String BOOK_TITLE = "title1";
    public static final String BOOK_TITLE_SEARCH_TERM = "ti";
    public static final String BOOK_DESCRIPTION = "description";
    public static final String BOOK_SUMMARY = "summary";
    public static final int BOOK_TOTAL_PAGES = 50;
    public static final String BOOK_LANGUAGE = "MK";
    public static final Double BOOK_RATING_FROM_WEB = 1.0;
    public static final Double BOOK_RATING_FROM_FIRM = 1.0;
    public static final String BOOK_IMAGE = "image1";
    public static final BookStatus BOOK_STATUS = BookStatus.PENDING_PURCHASE;
    public static final BookStatus BOOK_STATUS_VALID = BookStatus.REQUESTED;
    public static final String[] BOOK_GENRES = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};
    public static final Author AUTHOR = new Author(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507021"), "author1"
            , new HashSet<>(getBooks()));
    public static final Set<Author> AUTHORS = new HashSet<>(List.of(AUTHOR));
    public static final AuthorDTO AUTHOR_DTO = new AuthorDTO("author1");
    public static final Set<AuthorDTO> AUTHOR_DTOS = new HashSet<>(List.of(AUTHOR_DTO));

    public static List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        Book book1 = new Book(
                BOOK_ISBN,
                SharedServiceTestData.SKOPJE_OFFICE,
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
                AUTHORS,
                new ArrayList<>()
        );
        books.add(book1);
        Book book2 = new Book(
                "isbn2",
                SharedServiceTestData.SOFIJA_OFFICE,
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
                AUTHORS,
                new ArrayList<>()
        );
        books.add(book2);

        return books;
    }

    public static Book getBook() {
        return getBooks().getFirst();
    }

    public static List<BookDetailsDTO> getBookDetailsDTOs() {
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
                AUTHOR_DTOS,
                SharedServiceTestData.SKOPJE_OFFICE.getName(),
                ReviewTestData.getReviewResponseDTOs()
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
                AUTHOR_DTOS,
                SharedServiceTestData.SKOPJE_OFFICE.getName(),
                ReviewTestData.getReviewResponseDTOs()
        );

        return List.of(bookDTO1, bookDTO2);
    }

    public static BookDetailsDTO getBookDetailsDTO() {
        return getBookDetailsDTOs().getFirst();
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

    public BookInsertRequestDTO getBookInsertRequestDTO() {
        return new BookInsertRequestDTO(
                BOOK_ISBN,
                BOOK_TITLE,
                BOOK_DESCRIPTION,
                BOOK_LANGUAGE,
                BOOK_GENRES,
                BOOK_TOTAL_PAGES,
                BOOK_IMAGE,
                BOOK_RATING_FROM_WEB,
                AUTHOR_DTOS,
                SharedServiceTestData.SKOPJE_OFFICE.getName()
        );
    }
}
