package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemManagementAPIInvalidPathVariable {
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
    @NullSource
    @SneakyThrows
    void deleteBookItem_bookItemIdIsInvalid_returnsBadRequest(UUID bookItemId) {
        // given
        final String deleteBookItemPath = BOOK_ITEM_PATH + "/delete/" + bookItemId;

        // when & then
        mockMvc.perform(delete(deleteBookItemPath, bookItemId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to convert 'bookItemId' with value: 'null'"));

    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void  reportBookItemAsDamaged_bookItemIdIsInvalid_returnsBadRequest(UUID bookItemId) {
        // given
        final String reportBookItemPathAsDamaged = BOOK_ITEM_PATH + "/report-damage/" + bookItemId;

        // when & then
        mockMvc.perform(patch(reportBookItemPathAsDamaged, bookItemId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to convert 'bookItemId' with value: 'null'"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void reportBookItemAsLost_bookItemIdIsInvalid_returnsBadRequest(UUID bookItemId) {
        // given
        final String reportBookItemPathAsLost = BOOK_ITEM_PATH + "/report-lost/" + bookItemId;

        // when & then
        mockMvc.perform(patch(reportBookItemPathAsLost, bookItemId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to convert 'bookItemId' with value: 'null'"));

    }
}
