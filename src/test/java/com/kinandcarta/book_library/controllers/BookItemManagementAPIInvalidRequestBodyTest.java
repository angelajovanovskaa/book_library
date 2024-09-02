package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
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

@WebMvcTest(BookItemController.class)
class BookItemManagementAPIInvalidRequestBodyTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String INSERT_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/insert";

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

    @Test
    @SneakyThrows
    void createBookItem_isbnIsEmpty_returnsBadRequest() {
        performPostAndExpectBadRequestIsbn("");
    }

    @Test
    @SneakyThrows
    void createBookItem_isbnIsNull_returnsBadRequest() {
        performPostAndExpectBadRequestIsbn(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void createBookItem_officeNameIsInvalid_returnsBadRequest(String officeName) {
        performPostAndExpectBadRequestOfficeName(officeName);
    }

    @Test
    @SneakyThrows
    void createBookItem_officeNameIsEmpty_returnsBadRequest() {
        performPostAndExpectBadRequestOfficeName("");
    }

    @Test
    @SneakyThrows
    void createBookItem_officeNameIsNull_returnsBadRequest() {
        performPostAndExpectBadRequestOfficeName(null);
    }

    private void performPostAndExpectBadRequestIsbn(String isbn) throws Exception {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO(isbn, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // when & then
        mockMvc.perform(post(INSERT_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.isbn").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    private void performPostAndExpectBadRequestOfficeName(String officeName) throws Exception {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO(BookTestData.BOOK_ISBN, officeName);

        // when & then
        mockMvc.perform(post(INSERT_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }
}
