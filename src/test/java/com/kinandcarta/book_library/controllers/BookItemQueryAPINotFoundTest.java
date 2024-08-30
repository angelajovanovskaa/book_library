package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemQueryAPINotFoundTest {
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
    @SneakyThrows
    void getBookItem_bookItemDoesNotExist_returnsNotFoundRequest(){
        // given
        final String isbn = BookTestData.BOOK_INVALID_ISBN;
        given(bookItemQueryService.getBookItemsByBookIsbn(anyString(),anyString()))
                .willThrow(new BookItemNotFoundException(BookItemTestData.BOOK_ITEM_DIFFERENT_OFFICE_ID));

        // when & then
        mockMvc.perform(get(BOOK_ITEM_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME)
                .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.generalExceptionMessage").value("The bookItem with id: " + BookItemTestData.BOOK_ITEM_DIFFERENT_OFFICE_ID + " doesn't exist"));
    }
}
