package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookAlreadyBorrowedByUserException;
import com.kinandcarta.book_library.exceptions.BookItemAlreadyBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemIsNotBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.exceptions.EntitiesInDifferentOfficesException;
import com.kinandcarta.book_library.exceptions.LimitReachedForBorrowedBooksException;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.BOOK_ITEM_ID;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckoutResponseDTO;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookItem;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
class BookCheckoutManagementControllerTest {
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
    void borrowBookItem_userIdIsNullOrEmpty_returnsBadRequest() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(null, BOOK_ITEM_ID);

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userId").value("must not be null"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_bookItemIdIsNullOrEmpty_returnsBadRequest() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), null);

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.bookItemId").value("must not be null"));
    }

    @Test
    @SneakyThrows
    void returnBookItem_userIdIsNullOrEmpty_returnsBadRequest() {
        // given
        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(null, BOOK_ITEM_ID);

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userId").value("must not be null"));
    }

    @Test
    @SneakyThrows
    void returnBookItem_bookItemIdIsNullOrEmpty_returnsBadRequest() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), null);

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.bookItemId").value("must not be null"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_theEntitiesAreFromDifferentOffices_returnsBadRequest() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new EntitiesInDifferentOfficesException());

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "You can't borrow a book from a different office!"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_theBookItemIsBorrowed_returnsBadRequest() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookItemAlreadyBorrowedException(BOOK_ITEM_ID));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id: " + BOOK_ITEM_ID + " is already booked"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_hasInstanceOfTheBook_returnsBadRequest() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookAlreadyBorrowedByUserException(BOOK_ISBN));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The user already has an instance borrowed from the book with isbn: " + BOOK_ISBN));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_hasReachedBorrowLimit_returnsBadRequest() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new LimitReachedForBorrowedBooksException(3));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "You have reached the maximum number of borrowed books. 3 books borrowed already."));
    }

    @Test
    @SneakyThrows
    void returnBookItem_bookItemIsNotBorrowed_returnsBadRequest() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);
        given(bookCheckoutManagementService.returnBookItem(any())).willThrow(
                new BookItemIsNotBorrowedException(BOOK_ITEM_ID));

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id " + BOOK_ITEM_ID + " can't" +
                                " be returned because it is not borrowed."));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_theBookItemDoesNotExists_returnsNotFound() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);

        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookItemNotFoundException(BOOK_ITEM_ID));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id: " + BOOK_ITEM_ID + " doesn't exist"));
    }

    @Test
    @SneakyThrows
    void returnBookItem_theBookItemDoesNotExists_returnsNotFound() {
        // given
        User user = getUser();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), BOOK_ITEM_ID);

        given(bookCheckoutManagementService.returnBookItem(any())).willThrow(
                new BookItemNotFoundException(BOOK_ITEM_ID));

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id: " + BOOK_ITEM_ID + " doesn't exist"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_validRequest_returnsConfirmationMessage() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();

        BookCheckoutRequestDTO bookCheckoutRequestDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutManagementService.borrowBookItem(any())).willReturn(bookCheckoutResponseDTO);

        // when
        String jsonResult = mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookCheckoutResponseDTO result = objectMapper.readValue(jsonResult, BookCheckoutResponseDTO.class);

        // then
        assertThat(result).isEqualTo(bookCheckoutResponseDTO);
    }

    @Test
    @SneakyThrows
    void returnBookItem_validRequest_returnsConfirmationMessage() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();

        BookCheckoutRequestDTO bookCheckoutRequestDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutManagementService.returnBookItem(any())).willReturn(bookCheckoutResponseDTO);

        // when
        String jsonResult = mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookCheckoutResponseDTO result = objectMapper.readValue(jsonResult, BookCheckoutResponseDTO.class);

        // then
        assertThat(result).isEqualTo(bookCheckoutResponseDTO);
    }
}
