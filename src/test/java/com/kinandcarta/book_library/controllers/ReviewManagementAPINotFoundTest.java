package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewManagementAPINotFoundTest {
    public static final String REVIEW_BASE_PATH = "/reviews";
    public static final String INSERT_REVIEW_PATH = REVIEW_BASE_PATH + "/insert";
    public static final String UPDATE_REVIEW_PATH = REVIEW_BASE_PATH + "/update";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void insertReview_userNotFound_returnsNotFound() {
        // given
        UserNotFoundException exception = new UserNotFoundException(UserTestData.USER_EMAIL);

        given(reviewManagementService.insertReview(any())).willThrow(exception);

        // when & then
        performRequestAndExpectNotFound(true, INSERT_REVIEW_PATH, ReviewTestData.getReviewRequestDTO(),
                exception.getMessage());
    }

    @Test
    @SneakyThrows
    void insertReview_bookNotFound_returnsNotFound() {
        // given
        BookNotFoundException exception = new BookNotFoundException(BookTestData.BOOK_ISBN
                , SharedServiceTestData.SKOPJE_OFFICE_NAME);

        given(reviewManagementService.insertReview(any())).willThrow(exception);

        // when & then
        performRequestAndExpectNotFound(true, INSERT_REVIEW_PATH, ReviewTestData.getReviewRequestDTO(),
                exception.getMessage());
    }

    @Test
    @SneakyThrows
    void updateReview_reviewNotFound_returnsNotFound() {
        // given
        ReviewNotFoundException exception = new ReviewNotFoundException(UserTestData.USER_EMAIL,
                BookTestData.BOOK_ISBN);

        given(reviewManagementService.updateReview(any())).willThrow(exception);

        // when & then
        performRequestAndExpectNotFound(false, UPDATE_REVIEW_PATH, ReviewTestData.getReviewRequestDTO(),
                exception.getMessage());
    }

    @Test
    @SneakyThrows
    void deleteReview_reviewNotFound_returnsNotFound() {
        // given
        ReviewNotFoundException exception = new ReviewNotFoundException(ReviewTestData.REVIEW_ID);

        given(reviewManagementService.deleteReviewById(ReviewTestData.REVIEW_ID)).willThrow(exception);

        //when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/" + ReviewTestData.REVIEW_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.generalExceptionMessage").value(exception.getMessage()));
    }

    private void performRequestAndExpectNotFound(boolean isPost, String path, ReviewRequestDTO DTO,
                                                 String exceptionMessage) throws Exception {
        mockMvc.perform(((isPost ? post(path) : put(path)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.generalExceptionMessage").value(exceptionMessage));
    }
}
