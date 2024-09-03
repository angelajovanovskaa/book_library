package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.exceptions.BookItemAlreadyBorrowedException;
import com.kinandcarta.book_library.exceptions.BookItemIsNotBorrowedException;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemManagementAPIUnprocessableEntityTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String DELETE_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/delete/{bookItemId}";
    private static final String REPORT_BOOK_ITEM_AS_DAMAGED_PATH = BOOK_ITEM_PATH + "/report-damage/{bookItemId}";
    private static final String REPORT_BOOK_ITEM_AS_LOST_PATH = BOOK_ITEM_PATH + "/report-lost/{bookItemId}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deleteBookItem_alreadyBorrowed_returnsUnprocessableEntity() throws Exception {
        // given
        final UUID bookItemId = UUID.randomUUID();
        final String deleteBookItemPath = DELETE_BOOK_ITEM_PATH;

        BookItemAlreadyBorrowedException bookItemAlreadyBorrowedException =
                new BookItemAlreadyBorrowedException(bookItemId);

        given(bookItemManagementService.deleteById(any())).willThrow(bookItemAlreadyBorrowedException);

        // when & then
        mockMvc.perform(delete(deleteBookItemPath))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(bookItemAlreadyBorrowedException.getMessage()));
    }

    @Test
    void reportBookItemAsDamaged_notBorrowed_returnsUnprocessableEntity() throws Exception {
        // given
        final UUID bookItemId = UUID.randomUUID();
        final String reportBookItemPathAsDamaged = REPORT_BOOK_ITEM_AS_DAMAGED_PATH;

        BookItemIsNotBorrowedException bookItemIsNotBorrowedException = new BookItemIsNotBorrowedException(bookItemId);

        given(bookItemManagementService.reportBookItemAsDamaged(any())).willThrow(bookItemIsNotBorrowedException);

        // when & then
        mockMvc.perform(patch(reportBookItemPathAsDamaged))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(bookItemIsNotBorrowedException.getMessage()));
    }

    @Test
    void reportBookItemAsLost_notBorrowed_returnsUnprocessableEntity() throws Exception {
        // given
        final UUID bookItemId = UUID.randomUUID();
        final String reportBookItemPathAsLost = REPORT_BOOK_ITEM_AS_LOST_PATH;

        BookItemIsNotBorrowedException bookItemIsNotBorrowedException = new BookItemIsNotBorrowedException(bookItemId);

        given(bookItemManagementService.reportBookItemAsLost(any())).willThrow(bookItemIsNotBorrowedException);

        // when & then
        mockMvc.perform(patch(reportBookItemPathAsLost))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(bookItemIsNotBorrowedException.getMessage()));
    }
}
