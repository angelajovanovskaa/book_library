package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.exceptions.BookItemNotFoundException;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookItemManagementAPINotFoundTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String INSERT_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/insert";
    private static final String DELETE_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/delete/" + BookItemTestData.BOOK_ITEM_ID;
    private static final String REPORT_BOOK_ITEM_AS_DAMAGED =
            BOOK_ITEM_PATH + "/report-damage/" + BookItemTestData.BOOK_ITEM_ID;
    private static final String REPORT_BOOK_ITEM_AS_LOST =
            BOOK_ITEM_PATH + "/report-lost/" + BookItemTestData.BOOK_ITEM_ID;
    private static final String GENERAL_EXCEPTION_MESSAGE_JSON_PATH = "$.generalExceptionMessage";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookItemQueryService bookItemQueryService;

    @MockBean
    private BookItemManagementService bookItemManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void insertBookItem_bookDoesNotExist_returnsNotFound() throws Exception {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        BookNotFoundException bookNotFoundException = new BookNotFoundException(bookIdDTO.isbn());

        given(bookItemManagementService.insertBookItem(any())).willThrow(bookNotFoundException);
        // when & then
        mockMvc.perform(post(INSERT_BOOK_ITEM_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE_JSON_PATH).value(bookNotFoundException.getMessage()));
    }

    @Test
    void deleteBookItem_bookItemDoesNotExist_returnsNotFound() throws Exception {
        // given
        BookItemNotFoundException bookItemNotFoundException =
                new BookItemNotFoundException(BookItemTestData.BOOK_ITEM_ID);

        given(bookItemManagementService.deleteById(any())).willThrow(bookItemNotFoundException);

        // when & then
        mockMvc.perform(delete(DELETE_BOOK_ITEM_PATH))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE_JSON_PATH).value(bookItemNotFoundException.getMessage()));
    }

    @Test
    void reportBookItemAsDamaged_bookItemDoesNotExist_returnsNotFound() throws Exception {
        // given
        BookItemNotFoundException bookItemNotFoundException =
                new BookItemNotFoundException(BookItemTestData.BOOK_ITEM_ID);

        given(bookItemManagementService.reportBookItemAsDamaged(any())).willThrow(bookItemNotFoundException);

        // when & then
        mockMvc.perform(patch(REPORT_BOOK_ITEM_AS_DAMAGED))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE_JSON_PATH).value(bookItemNotFoundException.getMessage()));
    }

    @Test
    void reportBookItemAsLost_bookItemDoesNotExist_returnsNotFound() throws Exception {
        // given
        BookItemNotFoundException bookItemNotFoundException =
                new BookItemNotFoundException(BookItemTestData.BOOK_ITEM_ID);

        given(bookItemManagementService.reportBookItemAsLost(any())).willThrow(bookItemNotFoundException);

        // when & then
        mockMvc.perform(patch(REPORT_BOOK_ITEM_AS_LOST))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE_JSON_PATH).value(bookItemNotFoundException.getMessage()));
    }
}