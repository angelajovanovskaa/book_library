package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemQueryAPIQueryParamsMissingTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String DETAIL_JSON_PATH = "$.detail";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @Test
    @SneakyThrows
    void getBookItems_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetBookItemsWithMissingParam(
                SharedControllerTestData.BOOK_ISBN_PARAM,
                BookTestData.BOOK_ISBN,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT
        );
    }

    @Test
    @SneakyThrows
    void getBookItems_paramIsbnIsMissing_returnsBadRequest() {
        // given & when & then
        performGetBookItemsWithMissingParam(
                SharedControllerTestData.OFFICE_PARAM,
                SharedServiceTestData.SKOPJE_OFFICE_NAME,
                ErrorMessages.ISBN_NOT_PRESENT
        );
    }

    private void performGetBookItemsWithMissingParam(String missingParamName, String missingParamValue,
                                                     String expectedErrorMessage) throws Exception {
        mockMvc.perform(get(BOOK_ITEM_PATH)
                        .queryParam(missingParamName, missingParamValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(expectedErrorMessage));
    }
}
