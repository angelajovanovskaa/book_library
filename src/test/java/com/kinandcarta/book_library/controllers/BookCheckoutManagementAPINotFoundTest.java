package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.config.JwtService;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookCheckoutTestData;
import com.kinandcarta.book_library.utils.BookItemTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookCheckoutManagementAPINotFoundTest {
    private static final String BORROW_BOOK_ITEM_PATH = "/book-checkouts/borrow";
    private static final String RETURN_BOOK_ITEM_PATH = "/book-checkouts/return";

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void borrowBookItem_theBookItemDoesNotExists_returnsNotFound() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookItemNotFoundException(BookItemTestData.BOOK_ITEM_ID));

        // when & then
        performPostAndExpectNotFound(BORROW_BOOK_ITEM_PATH, bookCheckoutDTO);
    }

    @Test
    @SneakyThrows
    void returnBookItem_theBookItemDoesNotExists_returnsNotFound() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        given(bookCheckoutManagementService.returnBookItem(any())).willThrow(
                new BookItemNotFoundException(BookItemTestData.BOOK_ITEM_ID));

        // when & then
        performPostAndExpectNotFound(RETURN_BOOK_ITEM_PATH, bookCheckoutDTO);
    }

    private void performPostAndExpectNotFound(String path, Record DTO)
            throws Exception {
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        new BookItemNotFoundException(BookItemTestData.BOOK_ITEM_ID).getMessage()));
    }
}
