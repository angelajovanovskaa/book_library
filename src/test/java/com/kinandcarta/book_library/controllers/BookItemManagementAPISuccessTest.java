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
    void createBookItem_BookItemCreated_returnBookItemDTO(){
        // given
        final String insertBookItemPath = BOOK_ITEM_PATH + "/insert";

        BookIdDTO bookIdDTO = new BookIdDTO(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        BookItemDTO expectedBookItemDTO = BookItemTestData.getBookItemDTO();

        given(bookItemManagementService.insertBookItem(any()))
                .willReturn(expectedBookItemDTO);

        // when
        String jsonResult = mockMvc.perform(post(insertBookItemPath)
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
        final String deleteBookItemPath = BOOK_ITEM_PATH + "/delete/{bookItemId}";

        UUID bookItemId = BookItemTestData.BOOK_ITEM_ID;

        given(bookItemManagementService.deleteById(any())).willReturn(bookItemId);

        // when
        UUID result = performDeleteAndExpectOk(deleteBookItemPath, bookItemId);

        // then
        assertThat(result).isEqualTo(bookItemId);
    }

    @Test
    @SneakyThrows
    void reportBookItemAsDamaged_reportIsSuccessful_returnsConfirmationMessage() {
        // given
        final String returnBookItemPathAsDamaged = BOOK_ITEM_PATH + "/report-damage/{bookItemId}";

        UUID bookItemId = BookItemTestData.BOOK_ITEM_ID;

        given(bookItemManagementService.reportBookItemAsDamaged(any())).willReturn(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED);

        // when & then
        performPatchAndExpectOk(
                returnBookItemPathAsDamaged,
                bookItemId,
                BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_DAMAGED
        );
    }

    @Test
    @SneakyThrows
    void reportBookItemAsLost_reportIsSuccessful_returnsConfirmationMessage() {
        // given
        final String returnBookItemPathAsLost = BOOK_ITEM_PATH + "/report-lost/{bookItemId}";

        UUID bookItemId = BookItemTestData.BOOK_ITEM_ID;

        given(bookItemManagementService.reportBookItemAsLost(any())).willReturn(BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST);

        // when & then
        performPatchAndExpectOk(
                returnBookItemPathAsLost,
                bookItemId,
                BookItemResponseMessages.BOOK_ITEM_REPORTED_AS_LOST
        );
    }

    private UUID performDeleteAndExpectOk(String path, UUID id) throws Exception{
        String resultJson = mockMvc.perform(delete(path, id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(resultJson, UUID.class);
    }

    private void performPatchAndExpectOk(String path, UUID id, String expectedResponse) throws Exception {
        String result = mockMvc.perform(patch(path, id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        assertThat(result).isEqualTo(expectedResponse);
    }
}
