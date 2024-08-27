package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
class BookCheckoutManagementAPIInvalidRequestBodyTest {
    private static final String BORROW_BOOK_ITEM_PATH = "/book-checkouts/borrow";
    private static final String RETURN_BOOK_ITEM_PATH = "/book-checkouts/return";

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void borrowBookItem_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(userId, BookItemTestData.BOOK_ITEM_ID);

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userId").value("must not be null"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void borrowBookItem_bookItemIdIsNull_returnsBadRequest(UUID bookItemId) {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(UserTestData.USER_ID, bookItemId);

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.bookItemId").value("must not be null"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void returnBookItem_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(userId, BookItemTestData.BOOK_ITEM_ID);

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userId").value("must not be null"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void returnBookItem_bookItemIdIsNull_returnsBadRequest(UUID bookItemId) {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(UserTestData.USER_ID, bookItemId);

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.bookItemId").value("must not be null"));
    }
}
