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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewQueryAPIInvalidPathVariablesTest {
    private static final String GET_REVIEW_PATH = "/reviews/";
    private static final String DETAIL_JSON_PATH = "$.detail";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getReviewById_reviewIdIsBlank_returnsBadRequest(String reviewId) {
        // given

        // when & then
        performRequestAndExpectBadRequest(GET_REVIEW_PATH + reviewId,
                ErrorMessages.REVIEW_ID_PATH_VARIABLE_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewIdIsNull_returnsBadRequest() {
        // given

        // when & then
        performRequestAndExpectBadRequest(GET_REVIEW_PATH + null,
                ErrorMessages.REVIEW_ID_PATH_VARIABLE_FAILED_TO_CONVERT);
    }

    private void performRequestAndExpectBadRequest(String path, String message) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(message));
    }
}