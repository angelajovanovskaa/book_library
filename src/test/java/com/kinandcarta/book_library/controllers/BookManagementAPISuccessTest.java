package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.services.impl.BookManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookManagementAPISuccessTest {
    private static final String BOOK_PATH = "/books";

    @MockBean
    private BookManagementServiceImpl bookManagementService;

    @MockBean
    private BookQueryServiceImpl bookQueryService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @SneakyThrows
    void createBook_insertIsValid_returnsBookInsertRequestDTO() {
        // given
        final String postBookPath = BOOK_PATH + "/insert-book";

        given(bookManagementService.createBookWithAuthors(any())).willReturn(BookTestData.getBookDisplayDTO());


        // when
        String jsonResult = mockMvc.perform(post(postBookPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookTestData.getBookInsertRequestDTO())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookDisplayDTO actualResult = objectMapper.readValue(jsonResult, BookDisplayDTO.class);

        // then

        assertThat(actualResult).isEqualTo(BookTestData.getBookDisplayDTO());
    }

    @Test
    @SneakyThrows
    void deleteBook_deletionIsSuccessful_deletesBook() {
        // given
        final String deleteBookPath = BOOK_PATH + "/delete";

        given(bookManagementService.deleteBook(anyString(), anyString())).willReturn(BookTestData.getBookIdDto());

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);
        // when
        String jsonResult = mockMvc.perform(delete(deleteBookPath)
                        .queryParams(queryParamsValues)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        BookIdDTO actualResult = objectMapper.readValue(jsonResult, BookIdDTO.class);

        // then
        assertThat(actualResult).isEqualTo(BookTestData.getBookIdDto());
    }
}
