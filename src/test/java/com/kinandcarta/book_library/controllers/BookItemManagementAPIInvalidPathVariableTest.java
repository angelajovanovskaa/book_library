package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private static final String DELETE_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/delete/";
    private static final String DETAIL_JSON_PATH = "$.detail";
    private static final String ERROR_MESSAGE = "Failed to convert 'bookItemId' with value: 'null'";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @Test
    @SneakyThrows
    void deleteBookItem_bookItemIdIsNull_returnsBadRequest() {
        // given
        final String deleteBookItemPath = DELETE_BOOK_ITEM_PATH + null;

        // when & then
        mockMvc.perform(delete(deleteBookItemPath))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(ERROR_MESSAGE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/report-damage/", "/report-lost/"})
    @SneakyThrows
    void reportBookItem_bookItemIdIsNull_returnsBadRequest(String pathSuffix) {
        // given
        final String path = BOOK_ITEM_PATH + pathSuffix + null;

        // when & then
        performInvalidBookItemIdPatchRequest(path);
    }

    private void performInvalidBookItemIdPatchRequest(String path) throws Exception {
        mockMvc.perform(patch(path))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(ERROR_MESSAGE));
    }
}
