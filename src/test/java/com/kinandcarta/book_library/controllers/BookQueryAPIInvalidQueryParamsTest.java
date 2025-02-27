package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookQueryParamsTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookQueryAPIInvalidQueryParamsTest {
    private static final String BOOK_PATH = "/books";
    private static final String GET_BOOK_PATH = BOOK_PATH + "/get-book";
    private static final String GET_AVAILABLE_BOOK_PATH = BOOK_PATH + "/available";
    private static final String GET_REQUESTED_BOOK_PATH = BOOK_PATH + "/requested";
    private static final String GET_PAGINATED_AVAILABLE_BOOK_PATH = BOOK_PATH + "/paginated";
    private static final String GET_BY_TITLE_BOOK_PATH = BOOK_PATH + "/by-title";
    private static final String GET_BY_LANGUAGE_BOOK_PATH = BOOK_PATH + "/by-language";
    private static final String GET_BY_GENRES_BOOK_PATH = BOOK_PATH + "/by-genres";
    private static final String GENRES_PARAM = "genres";
    private static final String ERROR_FIELD_GET_BOOKS_OFFICE_NAME = "$.errorFields['getBooks.officeName']";
    private static final String ERROR_FIELD_GET_AVAILABLE_BOOKS_OFFICE_NAME = "$.errorFields['getAvailableBooks" +
            ".officeName']";
    private static final String ERROR_FIELD_GET_REQUESTED_BOOKS_OFFICE_NAME = "$.errorFields['getRequestedBooks" +
            ".officeName']";
    private static final String ERROR_FIELD_GET_PAGINATED_BOOKS_OFFICE_NAME = "$.errorFields['getPaginatedBooks" +
            ".officeName']";
    private static final String ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_OFFICE_NAME =
            "$.errorFields['getBooksBySearchTitle.officeName']";
    private static final String ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_TITLE_SEARCH_TERM =
            "$.errorFields['getBooksBySearchTitle.titleSearchTerm']";
    private static final String ERROR_FIELD_GET_BY_LANGUAGE_OFFICE_NAME = "$.errorFields['getBooksByLanguage" +
            ".officeName']";
    private static final String ERROR_FIELD_GET_BY_LANGUAGE_LANGUAGE = "$.errorFields['getBooksByLanguage.language']";
    private static final String ERROR_FIELD_GET_BY_GENRES_OFFICE_NAME = "$.errorFields['getBooksByGenres.officeName']";
    private static final String ERROR_FIELD_GET_BY_GENRES_GENRES = "$.errorFields['getBooksByGenres.genres']";
    private static final String ERROR_FIELD_GET_BOOK_OFFICE_NAME = "$.errorFields['getBook.officeName']";
    private static final String ERROR_FIELD_GET_BOOK_ISBN = "$.errorFields['getBook.isbn']";
    private static final String DETAIL = "$.detail";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("providePathsForGetOperationsWithNullOfficeParam")
    @SneakyThrows
    void getOperation_paramOfficeNameIsNull_returnsBadRequestForQueryParams(String path,
                                                                            MultiValueMap<String, String> queryParams) {
        // given & when & then
        performGetAndExpectBadRequest(path, queryParams, DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @MethodSource("providePathsForGetOperationsWithBlankOfficeParam")
    @SneakyThrows
    void getOperation_paramOfficeNameIsBlank_returnsBadRequestForQueryParams(String path,
                                                                             MultiValueMap<String, String> queryParams,
                                                                             String expectedField) {
        // when & then
        performGetAndExpectBadRequest(path, queryParams, expectedField, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @MethodSource("providePathsForGetOperationsWithEmptyOfficeParam")
    @SneakyThrows
    void getOperation_paramOfficeNameIsEmpty_returnsBadRequestForQueryParams(String path,
                                                                             MultiValueMap<String, String> queryParams,
                                                                             String expectedField) {
        // when & then
        performGetAndExpectBadRequest(path, queryParams, expectedField, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getBook_paramIsbnIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsIsbn("");

        // when & then
        performGetAndExpectBadRequest(GET_BOOK_PATH, queryParamsValues, ERROR_FIELD_GET_BOOK_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookQueryParamsTestData.createQueryParamsForGetByTitleWithCustomTitleSearchTerm("");

        // when & then
        performGetAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_TITLE_SEARCH_TERM, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookQueryParamsTestData.createQueryParamsForGetByLanguageWithCustomLanguage("");

        // when & then
        performGetAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_LANGUAGE_LANGUAGE, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   "})
    @SneakyThrows
    void getBooksByGenres_paramGenresIsBlank_returnsBadRequest(String genre) {
        // given
        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(GET_BY_GENRES_BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME).queryParam(GENRES_PARAM, genres))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByGenres.genres[0].<list element>']").value(
                        ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void getBooksByGenres_paramGenresIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookQueryParamsTestData.createQueryParamsForGetByGenreWithCustomGenre("");

        // when & then
        performGetAndExpectBadRequest(GET_BY_GENRES_BOOK_PATH, queryParamsValues, ERROR_FIELD_GET_BY_GENRES_GENRES,
                ErrorMessages.MUST_NOT_BE_EMPTY);
    }

    private void performGetAndExpectBadRequest(String path,
                                               MultiValueMap<String, String> queryParamValues,
                                               String errorField, String errorMessage)
            throws Exception {
        mockMvc.perform(get(path).queryParams(queryParamValues))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(errorField).value(errorMessage));
    }

    private static Stream<Arguments> providePathsForGetOperationsWithNullOfficeParam() {
        return Stream.of(
                Arguments.of(GET_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByBookWithCustomOfficeName(null)),
                Arguments.of(GET_BY_LANGUAGE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByLanguageWithCustomOfficeName(null)),
                Arguments.of(GET_BY_GENRES_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByGenreWithCustomOfficeName(null)),
                Arguments.of(GET_BY_TITLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByTitleWithCustomOfficeName(null)),
                Arguments.of(BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithNullOfficeName()),
                Arguments.of(GET_AVAILABLE_BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithNullOfficeName()),
                Arguments.of(GET_REQUESTED_BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithNullOfficeName()),
                Arguments.of(GET_PAGINATED_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithNullOfficeName())
        );
    }

    private static Stream<Arguments> providePathsForGetOperationsWithBlankOfficeParam() {
        return Stream.of(
                Arguments.of(GET_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByBookWithCustomOfficeName("  "),
                        ERROR_FIELD_GET_BOOK_OFFICE_NAME),
                Arguments.of(GET_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByBookWithCustomOfficeName("\t"),
                        ERROR_FIELD_GET_BOOK_OFFICE_NAME),
                Arguments.of(GET_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByBookWithCustomOfficeName("\n"),
                        ERROR_FIELD_GET_BOOK_OFFICE_NAME),
                Arguments.of(GET_BY_TITLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByTitleWithCustomOfficeName("  "),
                        ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_OFFICE_NAME),
                Arguments.of(GET_BY_TITLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByTitleWithCustomOfficeName("\t"),
                        ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_OFFICE_NAME),
                Arguments.of(GET_BY_TITLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByTitleWithCustomOfficeName("\n"),
                        ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_OFFICE_NAME),
                Arguments.of(GET_BY_LANGUAGE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByLanguageWithCustomOfficeName("  "),
                        ERROR_FIELD_GET_BY_LANGUAGE_OFFICE_NAME),
                Arguments.of(GET_BY_LANGUAGE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByLanguageWithCustomOfficeName("\t"),
                        ERROR_FIELD_GET_BY_LANGUAGE_OFFICE_NAME),
                Arguments.of(GET_BY_LANGUAGE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByLanguageWithCustomOfficeName("\n"),
                        ERROR_FIELD_GET_BY_LANGUAGE_OFFICE_NAME),
                Arguments.of(GET_BY_GENRES_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByGenreWithCustomOfficeName("  "),
                        ERROR_FIELD_GET_BY_GENRES_OFFICE_NAME),
                Arguments.of(GET_BY_GENRES_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByGenreWithCustomOfficeName("\t"),
                        ERROR_FIELD_GET_BY_GENRES_OFFICE_NAME),
                Arguments.of(GET_BY_GENRES_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByGenreWithCustomOfficeName("\n"),
                        ERROR_FIELD_GET_BY_GENRES_OFFICE_NAME),
                Arguments.of(BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("  "),
                        ERROR_FIELD_GET_BOOKS_OFFICE_NAME),
                Arguments.of(BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\t"),
                        ERROR_FIELD_GET_BOOKS_OFFICE_NAME),
                Arguments.of(BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\n"),
                        ERROR_FIELD_GET_BOOKS_OFFICE_NAME),
                Arguments.of(GET_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("  "),
                        ERROR_FIELD_GET_AVAILABLE_BOOKS_OFFICE_NAME),
                Arguments.of(GET_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\t"),
                        ERROR_FIELD_GET_AVAILABLE_BOOKS_OFFICE_NAME),
                Arguments.of(GET_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\n"),
                        ERROR_FIELD_GET_AVAILABLE_BOOKS_OFFICE_NAME),
                Arguments.of(GET_REQUESTED_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("  "),
                        ERROR_FIELD_GET_REQUESTED_BOOKS_OFFICE_NAME),
                Arguments.of(GET_REQUESTED_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\t"),
                        ERROR_FIELD_GET_REQUESTED_BOOKS_OFFICE_NAME),
                Arguments.of(GET_REQUESTED_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\n"),
                        ERROR_FIELD_GET_REQUESTED_BOOKS_OFFICE_NAME),
                Arguments.of(GET_PAGINATED_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("  "),
                        ERROR_FIELD_GET_PAGINATED_BOOKS_OFFICE_NAME),
                Arguments.of(GET_PAGINATED_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\t"),
                        ERROR_FIELD_GET_PAGINATED_BOOKS_OFFICE_NAME),
                Arguments.of(GET_PAGINATED_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithBlankOfficeName("\n"),
                        ERROR_FIELD_GET_PAGINATED_BOOKS_OFFICE_NAME)
        );
    }

    private static Stream<Arguments> providePathsForGetOperationsWithEmptyOfficeParam() {
        return Stream.of(
                Arguments.of(BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithEmptyOfficeName(),
                        ERROR_FIELD_GET_BOOKS_OFFICE_NAME),
                Arguments.of(GET_AVAILABLE_BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithEmptyOfficeName(),
                        ERROR_FIELD_GET_AVAILABLE_BOOKS_OFFICE_NAME),
                Arguments.of(GET_REQUESTED_BOOK_PATH, BookQueryParamsTestData.createQueryParamsWithEmptyOfficeName(),
                        ERROR_FIELD_GET_REQUESTED_BOOKS_OFFICE_NAME),
                Arguments.of(GET_PAGINATED_AVAILABLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsWithEmptyOfficeName(),
                        ERROR_FIELD_GET_PAGINATED_BOOKS_OFFICE_NAME),
                Arguments.of(GET_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByBookWithCustomOfficeName(""),
                        ERROR_FIELD_GET_BOOK_OFFICE_NAME),
                Arguments.of(GET_BY_TITLE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByTitleWithCustomOfficeName(""),
                        ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_OFFICE_NAME),
                Arguments.of(GET_BY_LANGUAGE_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByLanguageWithCustomOfficeName(""),
                        ERROR_FIELD_GET_BY_LANGUAGE_OFFICE_NAME),
                Arguments.of(GET_BY_GENRES_BOOK_PATH,
                        BookQueryParamsTestData.createQueryParamsForGetByGenreWithCustomOfficeName(""),
                        ERROR_FIELD_GET_BY_GENRES_OFFICE_NAME)
        );
    }
}
