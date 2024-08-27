package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
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
        BookInsertRequestDTO bookInsertRequestDTO = new BookInsertRequestDTO(isbn, BookTestData.BOOK_TITLE,
                BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                BookTestData.AUTHOR_DTOS, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value("must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void insertBook_paramIsbnIsEmpty_returnsBadRequest(String isbn) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO = new BookInsertRequestDTO(isbn, BookTestData.BOOK_TITLE,
                BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                BookTestData.AUTHOR_DTOS, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value("must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void insertBook_paramIsbnIsNull_returnsBadRequest(String isbn) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO = new BookInsertRequestDTO(isbn, BookTestData.BOOK_TITLE,
                BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                BookTestData.AUTHOR_DTOS, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value("must not be blank"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void insertBook_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO =
                new BookInsertRequestDTO(BookTestData.BOOK_ISBN, BookTestData.BOOK_TITLE,
                        BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                        BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                        BookTestData.AUTHOR_DTOS, officeName);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value("must not be blank"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void insertBook_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO =
                new BookInsertRequestDTO(BookTestData.BOOK_ISBN, BookTestData.BOOK_TITLE,
                        BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                        BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                        BookTestData.AUTHOR_DTOS, officeName);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value("must not be blank"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void insertBook_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";
        BookInsertRequestDTO bookInsertRequestDTO =
                new BookInsertRequestDTO(BookTestData.BOOK_ISBN, BookTestData.BOOK_TITLE,
                        BookTestData.BOOK_DESCRIPTION, BookTestData.BOOK_LANGUAGE, BookTestData.BOOK_GENRES,
                        BookTestData.BOOK_TOTAL_PAGES, BookTestData.BOOK_IMAGE, BookTestData.BOOK_RATING,
                        BookTestData.AUTHOR_DTOS, officeName);

        // when & then
        mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookInsertRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value("must not be blank"));
    }
}
