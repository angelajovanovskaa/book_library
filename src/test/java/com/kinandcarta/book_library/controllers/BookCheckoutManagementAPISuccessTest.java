package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookCheckoutTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static com.kinandcarta.book_library.utils.BookCheckoutTestData.getBookCheckoutResponseDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookCheckoutManagementAPISuccessTest {
    private static final String BORROW_BOOK_ITEM_PATH = "/book-checkouts/borrow";
    private static final String RETURN_BOOK_ITEM_PATH = "/book-checkouts/return";

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void borrowBookItem_validRequest_returnsBookCheckoutResponseDTO() {
        // given
        BookCheckoutRequestDTO bookCheckoutRequestDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutManagementService.borrowBookItem(any())).willReturn(bookCheckoutResponseDTO);

        // when
        String jsonResult = performPostAndExpectJsonResult(BORROW_BOOK_ITEM_PATH, bookCheckoutRequestDTO,
                status().isCreated());

        BookCheckoutResponseDTO result = objectMapper.readValue(jsonResult, BookCheckoutResponseDTO.class);

        // then
        assertThat(result).isEqualTo(bookCheckoutResponseDTO);
    }

    @Test
    @SneakyThrows
    void returnBookItem_validRequest_returnsBookCheckoutResponseDTO() {
        // given
        BookCheckoutRequestDTO bookCheckoutRequestDTO = BookCheckoutTestData.getBookCheckoutRequestDTO();
        BookCheckoutResponseDTO bookCheckoutResponseDTO = getBookCheckoutResponseDTO();

        given(bookCheckoutManagementService.returnBookItem(any())).willReturn(bookCheckoutResponseDTO);

        // when
        String jsonResult = performPostAndExpectJsonResult(RETURN_BOOK_ITEM_PATH, bookCheckoutRequestDTO,
                status().isOk());

        BookCheckoutResponseDTO result = objectMapper.readValue(jsonResult, BookCheckoutResponseDTO.class);

        // then
        assertThat(result).isEqualTo(bookCheckoutResponseDTO);
    }

    private String performPostAndExpectJsonResult(String path, Record DTO, ResultMatcher status) throws Exception {
        return mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }
}
