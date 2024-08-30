package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewQueryAPIQueryParamsMissingTest {
    public static final String REVIEW_BASE_PATH = "/reviews";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(
                        get(REVIEW_BASE_PATH).queryParam(SharedControllerTestData.BOOK_ISBN_PARAM,
                                BookTestData.BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(ErrorMessages.OFFICE_NAME_NOT_PRESENT));
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_isbnParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(ErrorMessages.ISBN_NOT_PRESENT));
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(ErrorMessages.OFFICE_NAME_NOT_PRESENT));
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews")
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(ErrorMessages.ISBN_NOT_PRESENT));
    }
}
