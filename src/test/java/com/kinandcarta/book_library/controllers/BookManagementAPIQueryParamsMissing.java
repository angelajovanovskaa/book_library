package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
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
class BookManagementAPIQueryParamsMissing {
    private static final String BOOK_PATH = "/books";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void deleteBook_paramIsbnIsBlank_returnsBadRequest(String isbn) {
        // given
        final String deleteBookPath = BOOK_PATH + "/delete";

        MultiValueMap<String, String> queryParamsValues = BookTestData.createQueryParamsIsbn(isbn);

        // when & then
        mockMvc.perform(delete(deleteBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['deleteBook.isbn']")
                        .value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void deleteBook_paramIsbnIsEmpty_returnsBadRequest(String isbn) {
        // given
        final String deleteBookPath = BOOK_PATH + "/delete";

        MultiValueMap<String, String> queryParamsValues = BookTestData.createQueryParamsIsbn(isbn);

        // when & then
        mockMvc.perform(delete(deleteBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['deleteBook.isbn']")
                        .value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void deleteBook_paramIsbnIsNull_returnsBadRequest(String isbn) {
        // given
        final String deleteBookPath = BOOK_PATH + "/delete";

        MultiValueMap<String, String> queryParamsValues = BookTestData.createQueryParamsIsbn(isbn);

        // when & then
        mockMvc.perform(delete(deleteBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail")
                        .value(ErrorMessages.ISBN_NOT_PRESENT));
    }
}
