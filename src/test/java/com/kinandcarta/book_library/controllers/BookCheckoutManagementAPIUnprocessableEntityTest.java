package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.exceptions.BookAlreadyBorrowedByUserException;
import com.kinandcarta.book_library.exceptions.BookItemAlreadyBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemIsNotBorrowedException;
import com.kinandcarta.book_library.exceptions.EntitiesInDifferentOfficesException;
import com.kinandcarta.book_library.exceptions.LimitReachedForBorrowedBooksException;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookCheckoutTestData;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class BookCheckoutManagementAPIUnprocessableEntityTest {
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

    @Test
    @SneakyThrows
    void borrowBookItem_theEntitiesAreFromDifferentOffices_returnsUnprocessableEntity() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new EntitiesInDifferentOfficesException());

        // when & then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "You can't borrow a book from a different office!"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_theBookItemIsBorrowed_returnsUnprocessableEntity() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookItemAlreadyBorrowedException(BookItemTestData.BOOK_ITEM_ID));

        // when & then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id: " + BookItemTestData.BOOK_ITEM_ID + " is already booked"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_hasInstanceOfTheBook_returnsUnprocessableEntity() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookAlreadyBorrowedByUserException(BookTestData.BOOK_ISBN));

        // when & then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The user already has an instance borrowed from the book with isbn: " +
                                BookTestData.BOOK_ISBN));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_hasReachedBorrowLimit_returnsUnprocessableEntity() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new LimitReachedForBorrowedBooksException(BookCheckoutTestData.MAX_NUMBER_OF_BORROWED_BOOKS));

        // when & then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "You have reached the maximum number of borrowed books. 3 books borrowed already."));
    }

    @Test
    @SneakyThrows
    void returnBookItem_bookItemIsNotBorrowed_returnsUnprocessableEntity() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        given(bookCheckoutManagementService.returnBookItem(any())).willThrow(
                new BookItemIsNotBorrowedException(BookItemTestData.BOOK_ITEM_ID));

        // when & then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id " + BookItemTestData.BOOK_ITEM_ID + " can't" +
                                " be returned because it is not borrowed."));
    }
}
