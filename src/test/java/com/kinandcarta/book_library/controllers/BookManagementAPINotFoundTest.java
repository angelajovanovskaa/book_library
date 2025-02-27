package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookQueryParamsTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookManagementAPINotFoundTest {
    private static final String BOOK_PATH = "/books";
    private static final String DELETE_BOOK_PATH = BOOK_PATH + "/delete";
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
    void deleteBook_isbnNotFound_returnsNotFoundRequest() {
        // given
        BookNotFoundException bookNotFoundException = new BookNotFoundException(BookTestData.BOOK_INVALID_ISBN);

        given(bookManagementService.deleteBook(anyString(), anyString())).willThrow(bookNotFoundException);

        MultiValueMap<String, String> queryParamsValues = BookQueryParamsTestData.createQueryParamsInvalidIsbn();

        // when & then
        mockMvc.perform(delete(DELETE_BOOK_PATH)
                        .queryParams(queryParamsValues))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE)
                        .value(bookNotFoundException.getMessage()));
    }
}
