package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.ErrorMessages;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewManagementAPIInvalidPathVariablesTest {
    public static final String REVIEW_BASE_PATH = "/reviews";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void deleteReview_reviewIdIsEmpty_returnsBadRequest(String reviewId) {
        // given

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/" + reviewId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(ErrorMessages.REVIEW_ID_PATH_VARIABLE_NOT_PRESENT));
    }

    @Test
    @SneakyThrows
    void deleteReview_reviewIdIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/" + null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(ErrorMessages.REVIEW_ID_PATH_VARIABLE_FAILED_TO_CONVERT));
    }
}