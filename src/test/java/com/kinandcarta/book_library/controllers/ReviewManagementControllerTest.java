package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.ReviewTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewManagementControllerTest {
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
    void insertNewReview_insertIsValid_returnsReviewResponseDTO() throws Exception {
        // given
        given(reviewManagementService.insertReview(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        String jsonResult =
                mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ReviewTestData.getReviewRequestDTO())))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        ReviewResponseDTO actualResult = objectMapper.readValue(jsonResult, ReviewResponseDTO.class);

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertNewReview_bookIsbnIsInvalid_returnsBadRequest(String invalidIsbn) {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(invalidIsbn, "email", "message", 5);

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertNewReview_userEmailIsInvalid_returnsBadRequest(String invalidEmail) {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("isbn", invalidEmail, "message", 5);

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertNewReview_messageIsInvalid_returnsBadRequest(String invalidMessage) {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("isbn", "email", invalidMessage, 5);

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void insertNewReview_ratingIsInvalid_returnsBadRequest() {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("isbn", "email", "message", 0);

        // when, then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReview_validRequest_returnsUpdatedReviewResponseDTO() throws Exception {
        // given
        ReviewResponseDTO expectedResponse = ReviewTestData.getReviewResponseDTO();
        Mockito.when(reviewManagementService.updateReview(any(ReviewRequestDTO.class)))
                .thenReturn(expectedResponse);

        // when
        String jsonResult = mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ReviewTestData.getReviewRequestDTO())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ReviewResponseDTO actualResponse = objectMapper.readValue(jsonResult, ReviewResponseDTO.class);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void deleteReview_validReviewId_returnsSuccessMessage() throws Exception {
        // given
        given(reviewManagementService.deleteReviewById(any())).willReturn(ReviewTestData.REVIEW_ID);

        // when
        String jsonResult =
                mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/" + ReviewTestData.REVIEW_ID))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Successfully deleted review with id " + ReviewTestData.REVIEW_ID))
                        .andReturn().getResponse().getContentAsString();

        // then
        assertThat(jsonResult).isEqualTo("Successfully deleted review with id " + ReviewTestData.REVIEW_ID);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "\t", "\n", "invalid-uuid"})
    void deleteReview_invalidReviewId_returnsBadRequest(String reviewId) throws Exception {
        // given

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/" + reviewId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReview_reviewDoesNotExist_returnsBadRequest() throws Exception {
        // given
        given(reviewManagementService.deleteReviewById(ReviewTestData.REVIEW_ID)).willThrow(
                new ReviewNotFoundException(ReviewTestData.REVIEW_ID));

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/"))
                .andExpect(status().isNotFound());
    }
}