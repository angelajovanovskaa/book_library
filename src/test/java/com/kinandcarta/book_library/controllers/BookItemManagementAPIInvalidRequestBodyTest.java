package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookItemManagementAPIInvalidRequestBodyTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String INSERT_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/insert";
    private static final String ERROR_FIELD_ISBN_PATH = "$.errorFields.isbn";
    private static final String ERROR_FIELD_OFFICE_NAME_PATH = "$.errorFields.officeName";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void createBookItem_isbnIsBlank_returnsBadRequest(String isbn) {
        performPostAndExpectBadRequestForInvalidIsbn(isbn);
    }

    @Test
    @SneakyThrows
    void createBookItem_isbnIsEmpty_returnsBadRequest() {
        performPostAndExpectBadRequestForInvalidIsbn("");
    }

    @Test
    @SneakyThrows
    void createBookItem_isbnIsNull_returnsBadRequest() {
        performPostAndExpectBadRequestForInvalidIsbn(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void createBookItem_officeNameIsBlank_returnsBadRequest(String officeName) {
        performPostAndExpectBadRequestForInvalidOfficeName(officeName);
    }

    @Test
    @SneakyThrows
    void createBookItem_officeNameIsEmpty_returnsBadRequest() {
        performPostAndExpectBadRequestForInvalidOfficeName("");
    }

    @Test
    @SneakyThrows
    void createBookItem_officeNameIsNull_returnsBadRequest() {
        performPostAndExpectBadRequestForInvalidOfficeName(null);
    }

    private void performPostAndExpectBadRequestForInvalidIsbn(String isbn) throws Exception {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO(isbn, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // when & then
        mockMvc.perform(post(INSERT_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ERROR_FIELD_ISBN_PATH).value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    private void performPostAndExpectBadRequestForInvalidOfficeName(String officeName) throws Exception {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO(BookTestData.BOOK_ISBN, officeName);

        // when & then
        mockMvc.perform(post(INSERT_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ERROR_FIELD_OFFICE_NAME_PATH).value(ErrorMessages.MUST_NOT_BE_BLANK));
    }
}
