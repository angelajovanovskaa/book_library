package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
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

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooks_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooks_paramOfficeIsEmpty_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooks_paramOfficeIsNull_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBook_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForGetBookMissingOfficeName(officeName);

        // when & then
        mockMvc.perform(get(GET_BOOK_PATH).queryParams(queryParamsValues)).andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_FIELD_GET_BOOK_OFFICE_NAME).value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBook_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForGetBookMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BOOK_OFFICE_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBook_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForGetBookMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BOOK_PATH, queryParamsValues, DETAIL,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBook_paramIsbnIsInvalid_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> queryParamsValues = BookTestData.createQueryParamsForGetBookMissingIsbn(isbn);

        // when & then
        performGetAndExpectBadRequest(GET_BOOK_PATH, queryParamsValues, ERROR_FIELD_GET_BOOK_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBook_paramIsbnIsEmpty_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> queryParamsValues = BookTestData.createQueryParamsForGetBookMissingIsbn(isbn);

        // when & then
        performGetAndExpectBadRequest(GET_BOOK_PATH, queryParamsValues, ERROR_FIELD_GET_BOOK_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBook_paramIsbnIsNull_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> queryParamsValues = BookTestData.createQueryParamsForGetBookMissingIsbn(isbn);

        // when & then
        performGetAndExpectBadRequest(GET_BOOK_PATH, queryParamsValues, DETAIL,
                ErrorMessages.ISBN_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(GET_AVAILABLE_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_AVAILABLE_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(GET_AVAILABLE_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_AVAILABLE_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // give & when & then
        performGetAndExpectBadRequest(GET_AVAILABLE_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(GET_REQUESTED_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_REQUESTED_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(GET_REQUESTED_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_REQUESTED_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(GET_REQUESTED_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(GET_PAGINATED_AVAILABLE_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM,
                officeName,
                ERROR_FIELD_GET_PAGINATED_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given & when & then

        performGetAndExpectBadRequest(GET_PAGINATED_AVAILABLE_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM,
                officeName,
                ERROR_FIELD_GET_PAGINATED_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(GET_PAGINATED_AVAILABLE_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM,
                officeName,
                DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForTitleMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForTitleMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForTitleMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH, queryParamsValues,
                DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleIsInvalid_returnsBadRequest(String titleSearchTerm) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForTitleMissingTitleSearchTerm(titleSearchTerm);

        // when & then
        performGetAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_TITLE_SEARCH_TERM, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleIsEmpty_returnsBadRequest(String titleSearchTerm) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForTitleMissingTitleSearchTerm(titleSearchTerm);

        // when & then
        performGetAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_TITLE_SEARCH_TERM_BOOKS_TITLE_SEARCH_TERM, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleIsNull_returnsBadRequest(String titleSearchTerm) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForTitleMissingTitleSearchTerm(titleSearchTerm);

        // when & then
        performGetAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH, queryParamsValues,
                DETAIL, ErrorMessages.TITLE_SEARCH_TERM_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsInvalid(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForLanguageMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_LANGUAGE_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForLanguageMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_LANGUAGE_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForLanguageMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues,
                DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsInvalid_returnsBadRequest(String language) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForLanguageMissingLanguage(language);

        // when & then

        performGetAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_LANGUAGE_LANGUAGE, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsEmpty_returnsBadRequest(String language) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForLanguageMissingLanguage(language);

        // when & then
        performGetAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_LANGUAGE_LANGUAGE, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsNull_returnsBadRequest(String language) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForLanguageMissingLanguage(language);

        // when & then
        performGetAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, queryParamsValues,
                DETAIL, ErrorMessages.LANGUAGE_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForGenresMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_GENRES_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_GENRES_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForGenresMissingOfficeName(officeName);

        // when & then

        performGetAndExpectBadRequest(GET_BY_GENRES_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_GET_BY_GENRES_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                BookTestData.createQueryParamsForGenresMissingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(GET_BY_GENRES_BOOK_PATH, queryParamsValues,
                DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   "})
    @SneakyThrows
    void getBooksByGenres_paramGenresIsInvalid_returnsBadRequest(String genre) {
        // given
        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(GET_BY_GENRES_BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME).queryParam(GENRES_PARAM, genres))
                .andExpect(status().isBadRequest()).andExpect(
                        jsonPath("$.errorFields['getBooksByGenres.genres[0].<list element>']").value(
                                ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByGenres_paramGenresIsEmpty_returnsBadRequest(String genre) {
        // given
        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(GET_BY_GENRES_BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME).queryParam(GENRES_PARAM, genres))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_FIELD_GET_BY_GENRES_GENRES).value(ErrorMessages.MUST_NOT_BE_EMPTY));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByGenres_paramGenresIsNull_returnsBadRequest(String genre) {
        // given
        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(GET_BY_GENRES_BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME).queryParam(GENRES_PARAM, genres))
                .andExpect(status().isBadRequest()).andExpect(jsonPath(DETAIL).value(ErrorMessages.GENRES_NOT_PRESENT));
    }

    private void performGetAndExpectBadRequest(String path, String param, String paramValue, String errorField, String
            errorMessage) throws Exception {
        mockMvc.perform(get(path).queryParam(param, paramValue))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(errorField).value(errorMessage));
    }

    private void performGetAndExpectBadRequest(String path,
                                               MultiValueMap<String, String> queryParamValues,
                                               String errorField, String errorMessage)
            throws Exception {
        mockMvc.perform(get(path).queryParams(queryParamValues))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}
