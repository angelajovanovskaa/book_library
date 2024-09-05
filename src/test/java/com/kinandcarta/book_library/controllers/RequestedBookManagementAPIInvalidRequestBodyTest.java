package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.config.JwtService;
import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.RequestedBookTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestedBookController.class)
@AutoConfigureMockMvc(addFilters = false)
class RequestedBookManagementAPIInvalidRequestBodyTest {
    private static final String REQUESTED_BOOKS_PATH = "/requested-books";
    private static final String CHANGE_BOOK_STATUS_PATH = REQUESTED_BOOKS_PATH + "/change-book-status";
    private static final String HANDLE_BOOK_LIKE_PATH = REQUESTED_BOOKS_PATH + "/handle-like";

    @MockBean
    private RequestedBookQueryService requestedBookQueryService;

    @MockBean
    private RequestedBookManagementService requestedBookManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void changeBookStatus_requestedBookIdIsNull_returnsBadRequest() {
        // given
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                new RequestedBookChangeStatusRequestDTO(null, BookStatus.IN_STOCK);

        // when & then
        performRequestAndExpectBadRequest(CHANGE_BOOK_STATUS_PATH, requestedBookChangeStatusRequestDTO,
                "$.errorFields.requestedBookId", ErrorMessages.MUST_NOT_BE_NULL, false);
    }

    @Test
    @SneakyThrows
    void changeBookStatus_bookStatusIsNull_returnsBadRequest() {
        // given
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                new RequestedBookChangeStatusRequestDTO(RequestedBookTestData.REQUESTED_BOOK_ID, null);

        // when & then
        performRequestAndExpectBadRequest(CHANGE_BOOK_STATUS_PATH, requestedBookChangeStatusRequestDTO,
                "$.errorFields.newBookStatus", ErrorMessages.MUST_NOT_BE_NULL, false);
    }

    @Test
    @SneakyThrows
    void handleRequestedBookLike_bookISBNIsNull_returnsBadRequest() {
        // given
        RequestedBookRequestDTO requestedBookRequestDTO = new RequestedBookRequestDTO(null, UserTestData.USER_EMAIL);

        // when & then
        performRequestAndExpectBadRequest(HANDLE_BOOK_LIKE_PATH, requestedBookRequestDTO,
                "$.errorFields.bookIsbn", ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void handleRequestedBookLike_userEmailIsNull_returnsBadRequest() {
        // given
        RequestedBookRequestDTO requestedBookRequestDTO = new RequestedBookRequestDTO(BookTestData.BOOK_ISBN, null);

        // when & then
        performRequestAndExpectBadRequest(HANDLE_BOOK_LIKE_PATH, requestedBookRequestDTO,
                "$.errorFields.userEmail", ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    private void performRequestAndExpectBadRequest(String path, Record DTO, String errorField, String errorMessage,
                                                   boolean isPost) throws Exception {
        mockMvc.perform((isPost ? post(path) : patch(path))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}
