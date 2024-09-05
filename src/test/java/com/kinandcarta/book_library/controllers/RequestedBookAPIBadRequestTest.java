package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.exceptions.RequestedBookStatusException;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import com.kinandcarta.book_library.utils.RequestedBookTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestedBookController.class)
@AutoConfigureMockMvc(addFilters = false)
class RequestedBookAPIBadRequestTest {
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
    void changeBookStatus_transitionIsInvalid_returnsBadRequest() {
        // given
        final String changeBookStatusPath = REQUESTED_BOOKS_PATH + "/change-book-status";
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                RequestedBookTestData.getRequestedBookChangeStatusRequestDTO();

        RequestedBookStatusException requestedBookStatusException =
                new RequestedBookStatusException("INVALID", "INVALID");

        given(requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO)).willThrow(
                requestedBookStatusException);

        // when & then
        mockMvc.perform(patch(changeBookStatusPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestedBookChangeStatusRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.generalExceptionMessage").value(
                        requestedBookStatusException.getMessage()));
    }
}