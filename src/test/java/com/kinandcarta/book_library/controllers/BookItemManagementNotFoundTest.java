package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemManagementNotFoundTest {
    private static final String BOOK_ITEM_PATH = "/book-items";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deleteBookItem_bookItemDoesNotExist_returnsNotFound() throws Exception {
        // given
        final UUID nonExistentBookItemId = UUID.randomUUID();
        final String deleteBookItemPath = BOOK_ITEM_PATH + "/delete/" + nonExistentBookItemId;

        BookItemNotFoundException bookItemNotFoundException = new BookItemNotFoundException(nonExistentBookItemId);

        given(bookItemManagementService.deleteById(any())).willThrow(bookItemNotFoundException);

        // when & then
        mockMvc.perform(delete(deleteBookItemPath))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(bookItemNotFoundException.getMessage()));
    }
}