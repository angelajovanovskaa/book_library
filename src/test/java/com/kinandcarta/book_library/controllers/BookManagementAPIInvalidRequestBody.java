package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
    private static final String INSERT_BOOK_PATH = BOOK_PATH + "/insert-book";

    private static final String ERROR_FIELD_ISBN = "$.errorFields.isbn";
    private static final String ERROR_FIELD_OFFICE_NAME = "$.errorFields.officeName";

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
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.createBookInsertRequestDTOPassingIsbn(isbn);

        // when & then
        performPostAndExpectBadRequest(INSERT_BOOK_PATH, bookInsertRequestDTO, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void insertBook_paramIsbnIsEmpty_returnsBadRequest() {
        // given
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.createBookInsertRequestDTOPassingIsbn("");

        // when & then
        performPostAndExpectBadRequest(INSERT_BOOK_PATH, bookInsertRequestDTO, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void insertBook_paramIsbnIsNull_returnsBadRequest() {
        // given
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.createBookInsertRequestDTOPassingIsbn(null);

        // when & then
        performPostAndExpectBadRequest(INSERT_BOOK_PATH, bookInsertRequestDTO, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void insertBook_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        BookInsertRequestDTO bookInsertRequestDTO =
                BookTestData.createBookInsertRequestDTOPassingOfficeName(officeName);

        // when & then
        performPostAndExpectBadRequest(INSERT_BOOK_PATH, bookInsertRequestDTO, ERROR_FIELD_OFFICE_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void insertBook_paramOfficeNameIsEmpty_returnsBadRequest() {
        // given
        BookInsertRequestDTO bookInsertRequestDTO =
                BookTestData.createBookInsertRequestDTOPassingOfficeName("");

        // when & then
        performPostAndExpectBadRequest(INSERT_BOOK_PATH, bookInsertRequestDTO, ERROR_FIELD_OFFICE_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void insertBook_paramOfficeNameIsNull_returnsBadRequest() {
        // given
        BookInsertRequestDTO bookInsertRequestDTO =
                BookTestData.createBookInsertRequestDTOPassingOfficeName(null);

        // when & then
        performPostAndExpectBadRequest(INSERT_BOOK_PATH, bookInsertRequestDTO, ERROR_FIELD_OFFICE_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    private void performPostAndExpectBadRequest(String path, Record DTO, String errorField, String errorMessage)
            throws Exception {
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}
