package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookQueryAPINotFoundTest {
    private static final String BOOK_PATH = "/books";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getBook_bookDoesNotExists_returnsNotFound() {
        // given
        final String getBookPath = BOOK_PATH + "/get-book";
        final String isbn = BookTestData.BOOK_INVALID_ISBN;

        BookNotFoundException bookNotFoundException = new BookNotFoundException(BookTestData.BOOK_INVALID_ISBN);

        given(bookQueryService.getBookByIsbn(anyString(), anyString()))
                .willThrow(bookNotFoundException);

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, isbn))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.generalExceptionMessage").value(bookNotFoundException.getMessage()));
    }
}
