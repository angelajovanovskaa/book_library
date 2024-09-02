package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemManagementAPIInvalidPathVariableTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String DETAIL_JSON_PATH = "$.detail";
    private static final String ERROR_MESSAGE = "Failed to convert 'bookItemId' with value: 'null'";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void deleteBookItem_bookItemIdIsInvalid_returnsBadRequest() {
        // given
        final String deleteBookItemPath = BOOK_ITEM_PATH + "/delete/" + null;

        // when & then
        mockMvc.perform(delete(deleteBookItemPath))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(ERROR_MESSAGE));
    }

    @Test
    @SneakyThrows
    void reportBookItemAsDamaged_bookItemIdIsInvalid_returnsBadRequest() {
        // given
        final String reportBookItemPathAsDamaged = BOOK_ITEM_PATH + "/report-damage/" + null;

        // when & then
        performInvalidBookItemIdPatchRequest(reportBookItemPathAsDamaged);
    }

    @Test
    @SneakyThrows
    void reportBookItemAsLost_bookItemIdIsInvalid_returnsBadRequest() {
        // given
        final String reportBookItemPathAsLost = BOOK_ITEM_PATH + "/report-lost/" + null;

        // when & then
        performInvalidBookItemIdPatchRequest(reportBookItemPathAsLost);
    }

    private void performInvalidBookItemIdPatchRequest(String path) throws Exception {
        mockMvc.perform(patch(path))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(ERROR_MESSAGE));
    }
}
