package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookQueryParamsTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookManagementAPIQueryParamsMissingTest {
    private static final String BOOK_PATH = "/books";
    private static final String DELETE_BOOK_PATH = BOOK_PATH + "/delete";
    private static final String ERROR_FIELD_ISBN = "$.errorFields['deleteBook.isbn']";
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
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void deleteBook_paramIsbnIsBlank_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsIsbn(isbn);

        // when & then
        performDeleteAndExpectBadRequest(DELETE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_ISBN, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void deleteBook_paramIsbnIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsIsbn("");

        // when & then
        performDeleteAndExpectBadRequest(DELETE_BOOK_PATH, queryParamsValues,
                ERROR_FIELD_ISBN, ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void deleteBook_paramIsbnIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsIsbn(null);

        // when & then
        mockMvc.perform(delete(DELETE_BOOK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(DETAIL)
                        .value(ErrorMessages.ISBN_NOT_PRESENT));
    }

    private void performDeleteAndExpectBadRequest(String path, MultiValueMap<String, String> queryParams,
                                                  String errorField, String errorMessage)
            throws Exception {
        mockMvc.perform(delete(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(queryParams))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}
