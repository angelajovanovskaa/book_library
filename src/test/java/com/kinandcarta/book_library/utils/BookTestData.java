package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.BookDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.OfficeTestData.OFFICE;
import static com.kinandcarta.book_library.utils.OfficeTestData.OTHER_OFFICE;

@UtilityClass
public class BookTestData {
    public static final String BOOK_ISBN = "isbn1";
    public static final BookId BOOK_ID = new BookId(BOOK_ISBN, OFFICE.getName());
    public static final String BOOK_TITLE = "title1";
    public static final String BOOK_TITLE_SEARCH_TERM = "ti";
    public static final String BOOK_LANGUAGE = "MK";
    public static final String[] BOOK_GENRES = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};
    public static final BookStatus BOOK_STATUS = BookStatus.REQUESTED;

    public static List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        Book book1 = new Book(
                "isbn1",
                OFFICE,
                "title1",
                "description1",
                "summary1",
                0,
                "MK",
                0.0,
                0.0,
                "image1",
                BookStatus.REQUESTED,
                BOOK_GENRES,
                new HashSet<>(),
                new ArrayList<>()
        );
        books.add(book1);
        Book book2 = new Book(
                "isbn2",
                OFFICE,
                "title2",
                "description2",
                "summary2",
                0,
                "MK",
                0.0,
                0.0,
                "image2",
                BookStatus.REQUESTED,
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

    public static Book getBookFromDifferentOffice() {
        return new Book(
                "isbn3",
                OTHER_OFFICE,
                "title3",
                "description3",
                "summary3",
                0,
                "MK",
                0.0,
                0.0,
                "image3",
                BookStatus.REQUESTED,
                BOOK_GENRES,
                new HashSet<>(),
                new ArrayList<>()
        );
    }

    public static List<BookDTO> getBookDTOs() {
        BookDTO bookDTO1 = new BookDTO(
                "isbn1",
                "title1",
                "description1",
                "MK",
                BOOK_GENRES,
                0,
                BOOK_STATUS,
                "image1",
                0.0,
                0.0,
                new HashSet<>()
        );
        BookDTO bookDTO2 = new BookDTO(
                "isbn2",
                "title2",
                "description2",
                "MK",
                BOOK_GENRES,
                0,
                BOOK_STATUS,
                "image2",
                0.0,
                0.0,
                new HashSet<>()
        );

        return List.of(bookDTO1, bookDTO2);
    }

    public static BookDTO getBookDTO() {
        return getBookDTOs().getFirst();
    }

    public static List<BookDisplayDTO> getBookDisplayDTOs() {
        BookDisplayDTO bookDisplayDTO1 = new BookDisplayDTO(
                "isbn1",
                "title1",
                "MK",
                "image1"
        );

        BookDisplayDTO bookDisplayDTO2 = new BookDisplayDTO(
                "isbn2",
                "title2",
                "MK",
                "image2"
        );

        return List.of(bookDisplayDTO1, bookDisplayDTO2);
    }

    public static BookDisplayDTO getBookDisplayDTO() {
        return getBookDisplayDTOs().getFirst();
    }
}
