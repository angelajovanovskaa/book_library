package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookQueryAPINotFoundTest {
    private static final String BOOK_PATH = "/books";
    private static final String getBookPath = BOOK_PATH + "/get-book";
    private static final String GENERAL_EXCEPTION_MESSAGE = "$.generalExceptionMessage";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getBook_bookDoesNotExists_returnsNotFound() {
        // given
        BookNotFoundException bookNotFoundException = new BookNotFoundException(BookTestData.BOOK_INVALID_ISBN);

        given(bookQueryService.getBookByIsbn(anyString(), anyString()))
                .willThrow(bookNotFoundException);

        // when & then
        mockMvc.perform(get(getBookPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME)
                        .queryParam(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_INVALID_ISBN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE).value(bookNotFoundException.getMessage()));
    }
}
