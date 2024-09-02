package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
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

@WebMvcTest(BookItemController.class)
class BookItemManagementAPIInvalidRequestBodyTest {
    private static final String BOOK_ITEM_PATH = "/book-items";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void createBookItem_isbnIsInvalid_returnsBadRequest(String isbn) {
        performPostAndExpectBadRequestIsbn(isbn);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void createBookItem_isbnIsEmpty_returnsBadRequest(String isbn) {
        performPostAndExpectBadRequestIsbn(isbn);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void createBookItem_isbnIsNull_returnsBadRequest(String isbn) {
        performPostAndExpectBadRequestIsbn(isbn);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void createBookItem_officeNameIsInvalid_returnsBadRequest(String officeName) {
        performPostAndExpectBadRequestOfficeName(officeName);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void createBookItem_officeNameIsEmpty_returnsBadRequest(String officeName) {
        performPostAndExpectBadRequestOfficeName(officeName);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void createBookItem_officeNameIsNull_returnsBadRequest(String officeName) {
        performPostAndExpectBadRequestOfficeName(officeName);
    }

    private void performPostAndExpectBadRequestIsbn(String isbn) throws Exception {
        // given
        final String insertBookItemPath = BOOK_ITEM_PATH + "/insert";
        BookIdDTO bookIdDTO = new BookIdDTO(isbn, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // when & then
        mockMvc.perform(post(insertBookItemPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    private void performPostAndExpectBadRequestOfficeName(String officeName) throws Exception {
        // given
        final String insertBookItemPath = BOOK_ITEM_PATH + "/insert";
        BookIdDTO bookIdDTO = new BookIdDTO(BookTestData.BOOK_ISBN, officeName);

        // when & then
        mockMvc.perform(post(insertBookItemPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

}
