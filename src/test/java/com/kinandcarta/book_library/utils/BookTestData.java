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
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class BookTestData {
    private static final String BOOK_SUMMARY = "summary";
    private static final AuthorDTO AUTHOR_DTO = new AuthorDTO("author1");
    public static final String BOOK_ISBN = "isbn1";
    public static final String BOOK_INVALID_ISBN = "invalidISBN";
    public static final BookId BOOK_ID = new BookId(BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE.getName());
    public static final BookIdDTO BOOK_ID_DTO = new BookIdDTO(BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE.getName());
    public static final String BOOK_TITLE = "title1";
    public static final String BOOK_TITLE_SEARCH_TERM = "ti";
    public static final String BOOK_DESCRIPTION = "description";
    public static final int BOOK_TOTAL_PAGES = 50;
    public static final String BOOK_LANGUAGE = "Macedonian";
    public static final Double BOOK_RATING = 1.0;
    public static final String BOOK_IMAGE = "image1";
    public static final String[] BOOK_GENRES = {Genre.MEMOIR.name(), Genre.ROMANCE.name()};
    public static final Author AUTHOR = new Author(UUID.fromString("cdaa6a7e-c933-43b7-b58d-d48054507021"), "author1"
            , new HashSet<>(getBooks()));
    public static final Set<Author> AUTHORS = new HashSet<>(List.of(AUTHOR));
    public static final Set<AuthorDTO> AUTHOR_DTOS = new HashSet<>(List.of(AUTHOR_DTO));
    private static final String LANGUAGE_PARAM = "language";
    private static final String GENRES_PARAM = "genres";

    public static List<Book> getBooks() {
        Book book1 = new Book(
                BOOK_ISBN,
                SharedServiceTestData.SKOPJE_OFFICE,
                BOOK_TITLE,
                BOOK_DESCRIPTION,
                BOOK_SUMMARY,
                BOOK_TOTAL_PAGES,
                BOOK_LANGUAGE,
                BOOK_RATING,
                BOOK_RATING,
                BOOK_IMAGE,
                BookStatus.REQUESTED,
                BOOK_GENRES,
                AUTHORS,
                new ArrayList<>()
        );
        Book book2 = new Book(
                "isbn2",
                SharedServiceTestData.SOFIJA_OFFICE,
                "title2",
                BOOK_DESCRIPTION,
                BOOK_SUMMARY,
                BOOK_TOTAL_PAGES,
                BOOK_LANGUAGE,
                BOOK_RATING,
                BOOK_RATING,
                BOOK_IMAGE,
                BookStatus.REQUESTED,
                BOOK_GENRES,
                AUTHORS,
                new ArrayList<>()
        );

        return List.of(book1, book2);
    }

    public static Book getBook() {
        return getBooks().getFirst();
    }

    public static List<BookDetailsDTO> getBookDetailsDTOs() {
        BookDetailsDTO bookDTO1 = new BookDetailsDTO(
                BOOK_ISBN,
                BOOK_TITLE,
                BOOK_DESCRIPTION,
                BOOK_LANGUAGE,
                BOOK_GENRES,
                BOOK_TOTAL_PAGES,
                BookStatus.REQUESTED,
                BOOK_IMAGE,
                BOOK_RATING,
                BOOK_RATING,
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
                BookStatus.REQUESTED,
                BOOK_IMAGE,
                BOOK_RATING,
                BOOK_RATING,
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
                BOOK_TITLE,
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
                BOOK_RATING,
                AUTHOR_DTOS,
                SharedServiceTestData.SKOPJE_OFFICE.getName()
        );
    }

    public BookIdDTO getBookIdDto() {
        return new BookIdDTO(BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE.getName());
    }

    public MultiValueMap<String, String> createQueryParamsForGetPaginatedBook() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.PAGE_SIZE_PARAM,
                String.valueOf(SharedServiceTestData.PAGE_SIZE));

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetBySearchTitle() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByLanguage() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByGenres() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(GENRES_PARAM, Arrays.toString(BookTestData.BOOK_GENRES));

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsMissingOfficeNameForGetBook(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsMissingOfficeNameForGetByTitle(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE_SEARCH_TERM);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsMissingTitleSearchTermForGetByTitle(String titleSearchTerm) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, titleSearchTerm);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsMissingOfficeNameForGetByLanguage(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsMissingLanguageForGetByLanguage(String language) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(LANGUAGE_PARAM, language);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsMissingOfficeNameGetByGenres(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(GENRES_PARAM, Arrays.toString(BOOK_GENRES));

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsMissingGenresGetByGenres(String genre) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(GENRES_PARAM, genre);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsIsbn(String isbn) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, isbn);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsInvalidIsbn() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_INVALID_ISBN);
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsWithOfficeAndISBN() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        return queryParamsValues;
    }

    public static BookInsertRequestDTO createBookInsertRequestDTOPassingIsbn(String isbn) {
        return new BookInsertRequestDTO(isbn, BookTestData.BOOK_TITLE,
                BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                BookTestData.AUTHOR_DTOS, SharedServiceTestData.SKOPJE_OFFICE_NAME);
    }

    public static BookInsertRequestDTO createBookInsertRequestDTOPassingOfficeName(String officeName) {
        return new BookInsertRequestDTO(BookTestData.BOOK_ISBN, BookTestData.BOOK_TITLE,
                BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                BookTestData.AUTHOR_DTOS, officeName);
    }
}
