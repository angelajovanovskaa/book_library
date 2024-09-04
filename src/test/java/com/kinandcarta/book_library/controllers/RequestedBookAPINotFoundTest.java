package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.RequestedBookTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestedBookController.class)
class RequestedBookAPINotFoundTest {
    private static final String REQUESTED_BOOKS_PATH = "/requested-books";

    @MockBean
    private RequestedBookQueryService requestedBookQueryService;

    @MockBean
    private RequestedBookManagementService requestedBookManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void changeBookStatus_requestedBookDoesNotExist_returnsNotFound() {
        // given
        final String changeBookStatusPath = REQUESTED_BOOKS_PATH + "/change-book-status";
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                RequestedBookTestData.getRequestedBookChangeStatusRequestDTO();

        RequestedBookNotFoundException requestedBookNotFoundException =
                new RequestedBookNotFoundException(RequestedBookTestData.REQUESTED_BOOK_ID);

        given(requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO)).willThrow(
                requestedBookNotFoundException);

        // when & then
        mockMvc.perform(patch(changeBookStatusPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestedBookChangeStatusRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        requestedBookNotFoundException.getMessage()));
    }

    @Test
    @SneakyThrows
    void handleRequestedBookLike_requestedBookDoesNotExist_returnsNotFound() {
        // given
        final String handleRequestedBookLikePath = REQUESTED_BOOKS_PATH + "/handle-like";
        RequestedBookRequestDTO requestedBookRequestDTO = RequestedBookTestData.getRequestedBookRequestDTO();

        RequestedBookNotFoundException requestedBookNotFoundException =
                new RequestedBookNotFoundException(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        given(requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO)).willThrow(
                requestedBookNotFoundException);

        // when & then
        performPostAndExpectNotFound(handleRequestedBookLikePath, requestedBookRequestDTO,
                requestedBookNotFoundException);
    }

    @Test
    @SneakyThrows
    void handleRequestedBookLike_UserDoesNotExist_returnsNotFound() {
        // given
        final String handleRequestedBookLikePath = REQUESTED_BOOKS_PATH + "/handle-like";
        RequestedBookRequestDTO requestedBookRequestDTO = RequestedBookTestData.getRequestedBookRequestDTO();

        UserNotFoundException userNotFoundException = new UserNotFoundException(requestedBookRequestDTO.userEmail());

        given(requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO)).willThrow(
                userNotFoundException);

        // when & then
        performPostAndExpectNotFound(handleRequestedBookLikePath, requestedBookRequestDTO, userNotFoundException);
    }

    private void performPostAndExpectNotFound(String path, Record DTO, Exception exception) throws Exception {
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(exception.getMessage()));
    }
}