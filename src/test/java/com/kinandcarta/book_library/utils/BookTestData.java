package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.experimental.UtilityClass;

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
    public static final Double BOOK_RATING_FROM_WEB = 0.0;
    public static final Double BOOK_RATING_FROM_FIRM = 0.0;
    public static final String BOOK_IMAGE = "image1";
    public static final BookStatus BOOK_STATUS = BookStatus.REQUESTED;
    public static final String[] BOOK_GENRES = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};

    public static List<Book> getBooks() {
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
                new HashSet<>(),
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
                new HashSet<>(),
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

    public static List<BookDTO> getBookDTOs() {
        BookDTO bookDTO1 = new BookDTO(
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
                new HashSet<>()
        );
        BookDTO bookDTO2 = new BookDTO(
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
                new HashSet<>()
        );

        return List.of(bookDTO1, bookDTO2);
    }

    public static BookDTO getBookDTO() {
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
}
