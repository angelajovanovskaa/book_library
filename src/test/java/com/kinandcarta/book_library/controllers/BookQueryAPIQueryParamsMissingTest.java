package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookQueryAPIQueryParamsMissingTest {
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

    @Test
    @SneakyThrows
    void getBooks_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        mockMvc.perform(get(BOOK_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getAvailableBooks_paramOfficeNameIsMissing_returnsBadRequest() {
        // given
        final String getAvailableBooksPath = BOOK_PATH + "/available";

        // when & then
        mockMvc.perform(get(getAvailableBooksPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getRequestedBooks_paramOfficeNameIsMissing_returnsBadRequest() {
        // given
        final String getRequestedBooksPath = BOOK_PATH + "/requested";

        // when & then
        mockMvc.perform(get(getRequestedBooksPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getPaginatedAvailableBooks_missingRequiredQueryParam_returnsBadRequest() {
        // given
        final String getPaginatedAvailableBooksPath = BOOK_PATH + "/paginated";

        //when & then
        mockMvc.perform(get(getPaginatedAvailableBooksPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getBooksBySearchTitle_paramTitleSearchTermIsMissing_returnsBadRequest() {
        // given
        final String getBooksBySearchTitlePath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getBooksBySearchTitlePath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'titleSearchTerm' is not present."));
    }

    @Test
    @SneakyThrows
    void getBooksBySearchTitle_paramOfficeNameIsMissing_returnsBadRequest() {
        // given
        final String getBooksBySearchTitlePath = BOOK_PATH + "/by-title";

        // when & then
        mockMvc.perform(get(getBooksBySearchTitlePath).queryParam(SharedControllerTestData.BOOK_TITLE_PARAM,
                        BookTestData.BOOK_TITLE))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getBooksByLanguage_paramLanguageIsMissing_returnsBadRequest() {
        // given
        final String getBooksByLanguagePath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getBooksByLanguagePath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'language' is not present."));
    }

    @Test
    @SneakyThrows
    void getBooksByLanguage_paramOfficeNameIsMissing_returnsBadRequest() {
        // given
        final String getBooksByLanguagePath = BOOK_PATH + "/by-language";

        // when & then
        mockMvc.perform(get(getBooksByLanguagePath).queryParam(LANGUAGE_PARAM,
                        BookTestData.BOOK_LANGUAGE))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getBooksByGenres_paramOfficeNameIsMissing_returnsBadRequest() {
        // given
        final String getBooksByGenresPath = BOOK_PATH + "/by-genres";

        // when & then
        mockMvc.perform(get(getBooksByGenresPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'genres' is not present."));
    }

    @Test
    @SneakyThrows
    void getBooksByGenres_paramGenresIsMissing_returnsBadRequest() {
        // given
        final String getBooksByGenresPath = BOOK_PATH + "/by-genres";

        // when & then
        mockMvc.perform(get(getBooksByGenresPath).queryParam(GENRES_PARAM, BookTestData.BOOK_GENRES))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }
}
