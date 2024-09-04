package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookQueryAPIQueryParamsMissingTest {
    private static final String BOOK_PATH = "/books";
    private static final String GENRES_PARAM = "genres";
    private static final String LANGUAGE_PARAM = "language";
    private static final String GET_BOOK_PATH = BOOK_PATH + "/get-book";
    private static final String GET_AVAILABLE_BOOK_PATH = BOOK_PATH + "/available";
    private static final String GET_REQUESTED_BOOK_PATH = BOOK_PATH + "/requested";
    private static final String GET_PAGINATED_AVAILABLE_BOOK_PATH = BOOK_PATH + "/paginated";
    private static final String GET_BY_TITLE_BOOK_PATH = BOOK_PATH + "/by-title";
    private static final String GET_BY_LANGUAGE_BOOK_PATH = BOOK_PATH + "/by-language";
    private static final String GET_BY_GENRES_BOOK_PATH = BOOK_PATH + "/by-genres";
    private static final String DETAIL = "$.detail";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getBooks_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(BOOK_PATH, DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBook_paramIsbnIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BOOK_PATH, DETAIL, ErrorMessages.ISBN_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBook_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BOOK_PATH, SharedControllerTestData.BOOK_ISBN_PARAM,
                BookTestData.BOOK_ISBN, DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_AVAILABLE_BOOK_PATH, DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_REQUESTED_BOOK_PATH, DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getPaginatedAvailableBooks_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_PAGINATED_AVAILABLE_BOOK_PATH, DETAIL,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBooksBySearchTitle_paramTitleSearchTermIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH,
                SharedControllerTestData.OFFICE_PARAM,
                SharedServiceTestData.SKOPJE_OFFICE_NAME, DETAIL,
                ErrorMessages.TITLE_SEARCH_TERM_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BY_TITLE_BOOK_PATH,
                SharedControllerTestData.BOOK_TITLE_PARAM,
                BookTestData.BOOK_TITLE, DETAIL,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH,
                SharedControllerTestData.OFFICE_PARAM,
                SharedServiceTestData.SKOPJE_OFFICE_NAME, DETAIL,
                ErrorMessages.LANGUAGE_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BY_LANGUAGE_BOOK_PATH, LANGUAGE_PARAM,
                BookTestData.BOOK_LANGUAGE, DETAIL,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BY_GENRES_BOOK_PATH, GENRES_PARAM,
                Arrays.toString(BookTestData.BOOK_GENRES), DETAIL,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getBooksByGenres_paramGenresIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(GET_BY_GENRES_BOOK_PATH, SharedControllerTestData.OFFICE_PARAM,
                SharedServiceTestData.SKOPJE_OFFICE_NAME, DETAIL,
                ErrorMessages.GENRES_NOT_PRESENT);
    }

    private void performGetRequestAndExpectBadRequest(String path, String errorField,
                                                      String errorMessage) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }

    private void performGetRequestAndExpectBadRequest(String path,
                                                      String param, String paramValue,
                                                      String errorField, String errorMessages)
            throws Exception {
        mockMvc.perform(get(path).queryParam(param, paramValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(errorField).value(errorMessages));
    }
}
