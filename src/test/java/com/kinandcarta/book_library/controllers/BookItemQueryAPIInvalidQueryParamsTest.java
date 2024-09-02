package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemQueryAPIInvalidQueryParamsTest {

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
    void getBookItems_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(officeName);
    }

    @Test
    @SneakyThrows
    void getBookItems_paramOfficeNameIsEmpty_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest("");
    }

    @Test
    @SneakyThrows
    void getBookItems_paramOfficeNameIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequestForNullParam(
                null,
                BookTestData.BOOK_ISBN,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookItems_paramIsbnIsBlank_returnsBadRequest(String isbn) {
        // given & when & then
        performGetAndExpectBadRequestForIsbn(isbn);
    }

    @Test
    @SneakyThrows
    void getBookItems_paramIsbnIsEmpty_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequestForIsbn("");
    }

    @Test
    @SneakyThrows
    void getBookItems_paramIsbnIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequestForNullParam(
                SharedServiceTestData.SKOPJE_OFFICE_NAME,
                null,
                "Required parameter 'isbn' is not present."
        );
    }

    private void performGetAndExpectBadRequest(String officeName) throws Exception {
        mockMvc.perform(get(BookItemQueryAPIInvalidQueryParamsTest.BOOK_ITEM_PATH)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookItems.officeName']").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    private void performGetAndExpectBadRequestForIsbn(String isbn) throws Exception {
        mockMvc.perform(get(BOOK_ITEM_PATH)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookItems.isbn']").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    private void performGetAndExpectBadRequestForNullParam(String officeName, String isbn, String expectedErrorMessage)
            throws Exception {
        mockMvc.perform(get(BookItemQueryAPIInvalidQueryParamsTest.BOOK_ITEM_PATH)
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, officeName)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(jsonPath("$.detail").value(expectedErrorMessage));
    }
}