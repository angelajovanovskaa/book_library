package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import com.kinandcarta.book_library.utils.BookItemResponseMessages;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookItemController.class)
class BookItemManagementAPISuccessTest {
    private static final String BOOK_ITEM_PATH = "/book-items";
    private static final String BOOK_ITEM_INSERT_PATH = BOOK_ITEM_PATH + "/insert";
    private static final String DELETE_BOOK_ITEM_PATH = BOOK_ITEM_PATH + "/delete/{bookItemId}";
    private static final String REPORT_BOOK_ITEM_AS_DAMAGED_PATH = BOOK_ITEM_PATH + "/report-damage/{bookItemId}";
    private static final String REPORT_BOOK_ITEM_AS_LOST_PATH = BOOK_ITEM_PATH + "/report-lost/{bookItemId}";

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
    void createBookItem_BookItemCreated_returnBookItemDTO() {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        BookItemDTO expectedBookItemDTO = BookItemTestData.getBookItemDTO();

        given(bookItemManagementService.insertBookItem(any()))
                .willReturn(expectedBookItemDTO);

        // when
        String jsonResult = mockMvc.perform(post(BOOK_ITEM_INSERT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookIdDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookItemDTO result = objectMapper.readValue(jsonResult, BookItemDTO.class);

        // then
        assertThat(result).isEqualTo(expectedBookItemDTO);
    }

    @Test
    @SneakyThrows
    void deleteBookItem_deleteIsSuccessful_deletesBookItem() {
        // given
        given(bookItemManagementService.deleteById(any())).willReturn(BookItemTestData.BOOK_ITEM_ID);

        // when
        String resultJson = mockMvc.perform(delete(DELETE_BOOK_ITEM_PATH, BookItemTestData.BOOK_ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString();

        UUID result = objectMapper.readValue(resultJson, UUID.class);
        // then
        assertThat(result).isEqualTo(BookItemTestData.BOOK_ITEM_ID);
    }

    @Test
    @SneakyThrows
    void reportBookItemAsDamaged_reportIsSuccessful_returnsConfirmationMessage() {
        // given
        given(bookItemManagementService.reportBookItemAsDamaged(any())).willReturn(
                BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);

        // when
        String result = performPatchAndExpectJsonResult(
                REPORT_BOOK_ITEM_AS_DAMAGED_PATH,
                BookItemTestData.BOOK_ITEM_ID
        );

        // then
        assertThat(result).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);
    }

    @Test
    @SneakyThrows
    void reportBookItemAsLost_reportIsSuccessful_returnsConfirmationMessage() {
        // given
        given(bookItemManagementService.reportBookItemAsLost(any())).willReturn(
                BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);

        // when
        String result = performPatchAndExpectJsonResult(
                REPORT_BOOK_ITEM_AS_LOST_PATH,
                BookItemTestData.BOOK_ITEM_ID
        );

        // then
        assertThat(result).isEqualTo(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);
    }

    private String performPatchAndExpectJsonResult(String path, UUID id) throws Exception {
        return mockMvc.perform(patch(path, id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }
}
