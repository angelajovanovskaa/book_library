package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import com.kinandcarta.book_library.utils.RequestedBookTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestedBookController.class)
class RequestedBookManagementAPISuccessTest {
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
    void changeBookStatus_validRequest_returnsRequestedBookResponseDTO() {
        // given
        final String changeBookStatusPath = REQUESTED_BOOKS_PATH + "/change-book-status";
        RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO =
                RequestedBookTestData.getRequestedBookChangeStatusRequestDTO();
        RequestedBookResponseDTO requestedBookResponseDTO = RequestedBookTestData.getRequestedBookResponseDTO();

        given(requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO)).willReturn(
                requestedBookResponseDTO);

        // when
        String jsonResult = performRequestAndExpectRequestedBookResponseDTO(changeBookStatusPath,
                requestedBookChangeStatusRequestDTO, false);

        RequestedBookResponseDTO result = objectMapper.readValue(jsonResult, RequestedBookResponseDTO.class);

        // then
        assertThat(result).isEqualTo(requestedBookResponseDTO);
    }

    @Test
    @SneakyThrows
    void handleRequestedBookLike_validRequest_returnsRequestedBookResponseDTO() {
        // given
        final String handleRequestedBookLikePath = REQUESTED_BOOKS_PATH + "/handle-like";
        RequestedBookRequestDTO requestedBookRequestDTO = RequestedBookTestData.getRequestedBookRequestDTO();
        RequestedBookResponseDTO requestedBookResponseDTO = RequestedBookTestData.getRequestedBookResponseDTO();

        given(requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO)).willReturn(
                requestedBookResponseDTO);

        // when
        String jsonResult = performRequestAndExpectRequestedBookResponseDTO(handleRequestedBookLikePath,
                requestedBookRequestDTO, true);

        RequestedBookResponseDTO result = objectMapper.readValue(jsonResult, RequestedBookResponseDTO.class);

        // then
        assertThat(result).isEqualTo(requestedBookResponseDTO);
    }

    private String performRequestAndExpectRequestedBookResponseDTO(String path, Record DTO, boolean isPost)
            throws Exception {
        return mockMvc.perform((isPost ? post(path) : patch(path))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }
}