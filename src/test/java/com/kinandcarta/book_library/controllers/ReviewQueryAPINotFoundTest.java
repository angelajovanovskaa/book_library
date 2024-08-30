package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.ReviewTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewQueryAPINotFoundTest {
    public static final String REVIEW_BASE_PATH = "/reviews";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getReviewById_reviewDoesNotExist_returnsNotFound() {
        // given
        ReviewNotFoundException exception = new ReviewNotFoundException(ReviewTestData.REVIEW_ID);

        given(reviewQueryService.getReviewById(ReviewTestData.REVIEW_ID)).willThrow(exception);

        //when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/" + ReviewTestData.REVIEW_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.generalExceptionMessage").value(exception.getMessage()));
    }
}
