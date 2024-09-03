package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
class BookCheckoutManagementAPIInvalidRequestBodyTest {
    private static final String BORROW_BOOK_ITEM_PATH = "/book-checkouts/borrow";
    private static final String RETURN_BOOK_ITEM_PATH = "/book-checkouts/return";

    private static final String ERROR_FIELD_USER_ID = "$.errorFields.userId";
    private static final String ERROR_FIELD_BOOK_ITEM_ID = "$.errorFields.bookItemId";


    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void borrowBookItem_userIdIsNull_returnsBadRequest() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(null, BookItemTestData.BOOK_ITEM_ID);

        // when && then
        performPostAndExpectBadRequest(BORROW_BOOK_ITEM_PATH, bookCheckoutDTO, ERROR_FIELD_USER_ID);
    }

    @Test
    @SneakyThrows
    void borrowBookItem_bookItemIdIsNull_returnsBadRequest() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(UserTestData.USER_ID, null);

        // when && then
        performPostAndExpectBadRequest(BORROW_BOOK_ITEM_PATH, bookCheckoutDTO, ERROR_FIELD_BOOK_ITEM_ID);
    }

    @Test
    @SneakyThrows
    void returnBookItem_userIdIsNull_returnsBadRequest() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(null, BookItemTestData.BOOK_ITEM_ID);

        // when && then
        performPostAndExpectBadRequest(RETURN_BOOK_ITEM_PATH, bookCheckoutDTO, ERROR_FIELD_USER_ID);
    }

    @Test
    @SneakyThrows
    void returnBookItem_bookItemIdIsNull_returnsBadRequest() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(UserTestData.USER_ID, null);

        // when && then
        performPostAndExpectBadRequest(RETURN_BOOK_ITEM_PATH, bookCheckoutDTO, ERROR_FIELD_BOOK_ITEM_ID);
    }

    private void performPostAndExpectBadRequest(String path, Record DTO, String errorField)
            throws Exception {
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(ErrorMessages.MUST_NOT_BE_NULL));
    }
}
