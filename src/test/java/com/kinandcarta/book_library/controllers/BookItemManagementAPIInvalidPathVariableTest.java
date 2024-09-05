package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemManagementAPIInvalidPathVariableTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String DELETE_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/delete/";
    private static final String REPORT_BOOK_ITEM_AS_DAMAGED = BOOK_ITEM_PATH + "/report-damage/";
    private static final String REPORT_BOOK_ITEM_AS_LOST = BOOK_ITEM_PATH + "/report-lost/";
    private static final String DETAIL_JSON_PATH = "$.detail";
    private static final String ERROR_MESSAGE = "Failed to convert 'bookItemId' with value: 'null'";
    private static final String ERROR_MESSAGE_BLANK = "Required path variable 'bookItemId' is not present.";

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
    @ValueSource(strings = {REPORT_BOOK_ITEM_AS_DAMAGED, REPORT_BOOK_ITEM_AS_LOST})
    @SneakyThrows
    void reportBookItem_bookItemIdIsNull_returnsBadRequest(String pathSuffix) {
        // given
        final String path = pathSuffix + null;

        // when & then
        mockMvc.perform(patch(path))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(ERROR_MESSAGE));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void deleteBookItem_bookItemIdIsBlank_returnsBadRequest() {
        // given
        final String deleteBookItemPath = DELETE_BOOK_ITEM_PATH + " ";

        // when & then
        mockMvc.perform(delete(deleteBookItemPath))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(ERROR_MESSAGE_BLANK));
    }

    @ParameterizedTest
    @MethodSource("providePathsAndBlankParametersForTest")
    void reportBookItem_bookItemIdIsBlank_returnsBadRequest(String path, String blankValue) throws Exception {
        // given
        final String fullPath = path + blankValue;

        // when & then
        mockMvc.perform(patch(fullPath))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(ERROR_MESSAGE_BLANK));
    }

    private static Stream<Arguments> providePathsAndBlankParametersForTest() {
        return Stream.of(
                Arguments.of(REPORT_BOOK_ITEM_AS_DAMAGED, " "),
                Arguments.of(REPORT_BOOK_ITEM_AS_DAMAGED, "\t"),
                Arguments.of(REPORT_BOOK_ITEM_AS_DAMAGED, "\n"),
                Arguments.of(REPORT_BOOK_ITEM_AS_LOST, " "),
                Arguments.of(REPORT_BOOK_ITEM_AS_LOST, "\t"),
                Arguments.of(REPORT_BOOK_ITEM_AS_LOST, "\n")
        );
    }
}
