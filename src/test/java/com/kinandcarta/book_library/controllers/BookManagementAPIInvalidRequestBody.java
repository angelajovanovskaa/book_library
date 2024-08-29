package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookManagementAPIInvalidRequestBody {
    private static final String BOOK_PATH = "/books";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void insertBook_paramIsbnIsBlank_returnsBadRequest(String isbn) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.createBookInsertRequestDTOPassingIsbn(isbn);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void insertBook_paramIsbnIsEmpty_returnsBadRequest(String isbn) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.createBookInsertRequestDTOPassingIsbn(isbn);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void insertBook_paramIsbnIsNull_returnsBadRequest(String isbn) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.createBookInsertRequestDTOPassingIsbn(isbn);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void insertBook_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO =
                BookTestData.createBookInsertRequestDTOPassingOfficeName(officeName);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void insertBook_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO =
                BookTestData.createBookInsertRequestDTOPassingOfficeName(officeName);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void insertBook_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO =
                BookTestData.createBookInsertRequestDTOPassingOfficeName(officeName);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }
}
