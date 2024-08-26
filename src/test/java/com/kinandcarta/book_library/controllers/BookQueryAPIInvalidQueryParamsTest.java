package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookQueryAPIInvalidQueryParamsTest {
    private static final String BOOK_PATH = "/books";
    private static final String GENRES_PARAM = "genres";
    private static final String LANGUAGE_PARAM = "language";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooks_paramOfficeNameBlank_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooks_paramOfficeIsEmpty_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooks_paramOfficeIsNull_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(BOOK_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBook_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBook.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBook_paramOfficeNameIsEmpty_returnsBadRequest(String isbn) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM, isbn)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBook.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBook_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBook_paramIsbnBlank_returnsBadRequest(String isbn) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBook.isbn']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBook_paramIsbnIsEmpty_returnsBadRequest(String isbn) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBook.isbn']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBook_paramIsbnIsNull_returnsBadRequest(String isbn) {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'isbn' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/available";

        //when & then
        mockMvc.perform(get(getAvailableBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getAvailableBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/available";

        //when & then
        mockMvc.perform(get(getAvailableBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getAvailableBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/available";

        //when & then
        mockMvc.perform(get(getAvailableBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String getRequestedBooksPath = BOOK_PATH + "/requested";

        // when & then
        mockMvc.perform(get(getRequestedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getRequestedBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getRequestedBooksPath = BOOK_PATH + "/requested";

        // when & then
        mockMvc.perform(get(getRequestedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getRequestedBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getRequestedBooksPath = BOOK_PATH + "/requested";

        // when & then
        mockMvc.perform(get(getRequestedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String getAvailablePaginatedBooksPath = BOOK_PATH + "/paginated";

        // when & then
        mockMvc.perform(
                        get(getAvailablePaginatedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getPaginatedBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getAvailablePaginatedBooksPath = BOOK_PATH + "/paginated";

        // when & then
        mockMvc.perform(
                        get(getAvailablePaginatedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getPaginatedBooks.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getAvailablePaginatedBooksPath = BOOK_PATH + "/paginated";

        // when & then
        mockMvc.perform(
                        get(getAvailablePaginatedBooksPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                officeName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;

    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE_SEARCH_TERM)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksBySearchTitle.officeName']").value("must not be blank"))
        ;

    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE_SEARCH_TERM)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksBySearchTitle.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE_SEARCH_TERM)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleIsBlank_returnsBadRequest(String titleSearchTerm) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, titleSearchTerm)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorFields['getBooksBySearchTitle.titleSearchTerm']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleIsEmpty_returnsBadRequest(String titleSearchTerm) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, titleSearchTerm)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorFields['getBooksBySearchTitle.titleSearchTerm']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksBySearchTitle_paramSearchTitleIsNull_returnsBadRequest(String titleSearchTerm) {
        // given
        final String getByTitleBooksPath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getByTitleBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_TITLE_PARAM, titleSearchTerm)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'titleSearchTerm' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsBlank(String officeName) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByLanguage.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByLanguage.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsBlank_returnsBadRequest(String language) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(LANGUAGE_PARAM, language)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByLanguage.language']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsEmpty_returnsBadRequest(String language) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(LANGUAGE_PARAM, language)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByLanguage.language']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsNull_returnsBadRequest(String language) {
        // given
        final String getByLanguageBooksPath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getByLanguageBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(LANGUAGE_PARAM, language)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'language' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(GENRES_PARAM, BookTestData.BOOK_GENRES)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByGenres.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(GENRES_PARAM, BookTestData.BOOK_GENRES)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByGenres.officeName']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(GENRES_PARAM, BookTestData.BOOK_GENRES)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   "})
    @SneakyThrows
    void getBooksByGenres_paramGenresIsBlank_returnsBadRequest(String genre) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(GENRES_PARAM, genres)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByGenres.genres[0].<list element>']").value("must not be blank"))
        ;
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getBooksByGenres_paramGenresIsEmpty_returnsBadRequest(String genre) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(GENRES_PARAM, genres)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields['getBooksByGenres.genres']").value("must not be empty"))
        ;
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getBooksByGenres_paramGenresIsNull_returnsBadRequest(String genre) {
        // given
        final String getByGenresBooksPath = BOOK_PATH + "/by-genres";

        String[] genres = {genre};

        // when & then
        mockMvc.perform(get(getByGenresBooksPath)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(GENRES_PARAM, genres)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'genres' is not present."))
        ;
    }
}
