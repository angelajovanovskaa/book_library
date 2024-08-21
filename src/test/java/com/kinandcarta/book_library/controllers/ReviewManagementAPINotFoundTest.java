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
        given(reviewManagementService.insertReview(any())).willThrow(
                new UserNotFoundException(UserTestData.USER_EMAIL));

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ReviewTestData.getReviewRequestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void insertReview_bookNotFound_returnsNotFound() {
        // given
        given(reviewManagementService.insertReview(any())).willThrow(new BookNotFoundException(BookTestData.BOOK_ISBN
                , SharedServiceTestData.SKOPJE_OFFICE_NAME));

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ReviewTestData.getReviewRequestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void updateReview_reviewNotFound_returnsNotFound() {
        // given
        ReviewRequestDTO reviewRequestDTO = ReviewTestData.getReviewRequestDTO();

        given(reviewManagementService.updateReview(any()))
                .willThrow(new ReviewNotFoundException(UserTestData.USER_EMAIL, BookTestData.BOOK_ISBN));

        // when & then
        String jsonContent = new ObjectMapper().writeValueAsString(reviewRequestDTO);

        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteReview_reviewIdIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No static resource reviews/delete."));
    }

    @Test
    @SneakyThrows
    void deleteReview_reviewDoesNotExist_returnsNotFound() {
        // given
        given(reviewManagementService.deleteReviewById(ReviewTestData.REVIEW_ID)).willThrow(
                new ReviewNotFoundException(ReviewTestData.REVIEW_ID));

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/"))
                .andExpect(status().isNotFound());
    }
}
