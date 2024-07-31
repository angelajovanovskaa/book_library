package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Language;
import com.kinandcarta.book_library.exceptions.*;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookItem;
import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookCheckoutManagementController.class)
class BookCheckoutManagementControllerTest {
    private static final String BORROW_BOOK_ITEM_PATH = "/bookCheckoutManagement/borrow";
    private static final String RETURN_BOOK_ITEM_PATH = "/bookCheckoutManagement/return";
    private static final Office SOFIJA_OFFICE = new Office("Sofija");
    private static final LocalDate DATE_NOW = LocalDate.now();

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void borrowBookItem_userIdIsNull_returnsBadRequest() {
        // given
        BookItem bookItem = getBookItem();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(null, bookItem.getId());

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
    void borrowBookItem_bookItemIdIsNull_returnsBadRequest() {
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
    void borrowBookItem_theBookItemDoesNotExists_returnsNotFound() {
        // given
        User user = getUser();
        UUID bookItemId = UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf23");

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItemId);

        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookItemNotFoundException(bookItemId));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id: " + bookItemId + " doesn't exist"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_theEntitiesAreFromDifferentOffices_returnsBadRequest() {
        // given
        User user = getUser();
        Book book = new Book("2222", SOFIJA_OFFICE, "Spiderman", "book description", "some summary", 555,
                String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.IN_STOCK, new String[5],
                new HashSet<>(), new ArrayList<>());
        BookItem bookItem =
                new BookItem(UUID.fromString("0a47a03f-dbc5-4b0c-9187-07e57f188be5"), BookItemState.AVAILABLE, book);

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());
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
        BookItem bookItem = getBookItem();
        UUID bookItemId = bookItem.getId();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItemId);
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookItemAlreadyBorrowedException(bookItemId));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id: " + bookItemId + " is already booked"));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_hasInstanceOfTheBook_returnsBadRequest() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();
        Book book = bookItem.getBook();
        String isbn = book.getIsbn();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new BookAlreadyBorrowedByUserException(isbn));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The user already has an instance borrowed from the book with isbn: " + isbn));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_hasReachedBorrowLimit_returnsBadRequest() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());
        given(bookCheckoutManagementService.borrowBookItem(any())).willThrow(
                new LimitReachedForBorrowedBooksException(3));

        // when && then
        mockMvc.perform(post(BORROW_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "You have reached the maximum number of borrowed books. " + 3 + " books borrowed already."));
    }

    @Test
    @SneakyThrows
    void returnBookItem_theBookItemDoesNotExists_returnsNotFound() {
        // given
        User user = getUser();
        UUID bookItemId = UUID.fromString("aa74a33b-b394-447f-84c3-72220ecfcf23");

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItemId);

        given(bookCheckoutManagementService.returnBookItem(any())).willThrow(
                new BookItemNotFoundException(bookItemId));

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id: " + bookItemId + " doesn't exist"));
    }

    @Test
    @SneakyThrows
    void returnBookItem_bookItemIsNotBorrowed_returnsBadRequest() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();
        UUID bookItemId = bookItem.getId();

        BookCheckoutRequestDTO bookCheckoutDTO = new BookCheckoutRequestDTO(user.getId(), bookItemId);
        given(bookCheckoutManagementService.returnBookItem(any())).willThrow(
                new BookItemIsNotBorrowedException(bookItemId));

        // when && then
        mockMvc.perform(post(RETURN_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCheckoutDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        "The bookItem with id " + bookItemId + " can't" + " be returned because it is not borrowed."));
    }

    @Test
    @SneakyThrows
    void borrowBookItem_validRequest_returnsConfirmationMessage() {
        // given
        User user = getUser();
        BookItem bookItem = getBookItem();
        Book book = bookItem.getBook();

        BookCheckoutRequestDTO bookCheckoutRequestDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());
        BookCheckoutResponseDTO bookCheckoutResponseDTO = new BookCheckoutResponseDTO(book.getTitle(), book.getIsbn(),
                DATE_NOW, null, DATE_NOW.plusDays(5));

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
        Book book = bookItem.getBook();

        BookCheckoutRequestDTO bookCheckoutRequestDTO = new BookCheckoutRequestDTO(user.getId(), bookItem.getId());
        BookCheckoutResponseDTO bookCheckoutResponseDTO = new BookCheckoutResponseDTO(book.getTitle(), book.getIsbn(),
                DATE_NOW, DATE_NOW, DATE_NOW.plusDays(5));

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
