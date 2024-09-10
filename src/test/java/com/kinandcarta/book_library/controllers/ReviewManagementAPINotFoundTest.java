package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.config.JwtService;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewManagementAPINotFoundTest {
    private static final String REVIEW_BASE_PATH = "/reviews";
    private static final String DELETE_REVIEW_PATH = REVIEW_BASE_PATH + "/delete/";
    private static final String INSERT_REVIEW_PATH = REVIEW_BASE_PATH + "/insert";
    private static final String UPDATE_REVIEW_PATH = REVIEW_BASE_PATH + "/update";
    private static final String GENERAL_EXCEPTION_MESSAGE = "$.generalExceptionMessage";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

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
        BookNotFoundException exception =
                new BookNotFoundException(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        given(reviewManagementService.insertReview(any())).willThrow(exception);

        // when & then
        performRequestAndExpectNotFound(true, INSERT_REVIEW_PATH, ReviewTestData.getReviewRequestDTO(),
                exception.getMessage());
    }

    @Test
    @SneakyThrows
    void updateReview_reviewNotFound_returnsNotFound() {
        // given
        ReviewNotFoundException exception =
                new ReviewNotFoundException(UserTestData.USER_EMAIL, BookTestData.BOOK_ISBN);

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
        mockMvc.perform(delete(DELETE_REVIEW_PATH + ReviewTestData.REVIEW_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE).value(exception.getMessage()));
    }

    private void performRequestAndExpectNotFound(boolean isPost, String path, ReviewRequestDTO reviewRequestDTO,
                                                 String exceptionMessage) throws Exception {
        mockMvc.perform((isPost ? post(path) : put(path))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(GENERAL_EXCEPTION_MESSAGE).value(exceptionMessage));
    }
}