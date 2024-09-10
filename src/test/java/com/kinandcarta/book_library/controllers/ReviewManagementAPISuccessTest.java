package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.ReviewTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false)
class ReviewManagementAPISuccessTest {
    private static final String REVIEW_BASE_PATH = "/reviews";
    private static final String REVIEW_DELETE_PATH = REVIEW_BASE_PATH + "/delete/";
    private static final String INSERT_REVIEW_PATH = REVIEW_BASE_PATH + "/insert";
    private static final String UPDATE_REVIEW_PATH = REVIEW_BASE_PATH + "/update";

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
    void insertReview_insertIsValid_returnsReviewResponseDTO() {
        // given
        given(reviewManagementService.insertReview(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        String jsonResult =
                performRequestAndExpectSuccess(true, INSERT_REVIEW_PATH, ReviewTestData.getReviewRequestDTO());

        ReviewResponseDTO actualResult = objectMapper.readValue(jsonResult, ReviewResponseDTO.class);

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void updateReview_validRequest_returnsUpdatedReviewResponseDTO() {
        // given
        given(reviewManagementService.updateReview(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        String jsonResult =
                performRequestAndExpectSuccess(false, UPDATE_REVIEW_PATH, ReviewTestData.getReviewRequestDTO());

        ReviewResponseDTO actualResponse = objectMapper.readValue(jsonResult, ReviewResponseDTO.class);

        // then
        assertThat(actualResponse).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void deleteReview_validReviewId_returnsSuccessMessage() {
        // given
        given(reviewManagementService.deleteReviewById(any())).willReturn(ReviewTestData.REVIEW_ID);

        // when & then
        mockMvc.perform(delete(REVIEW_DELETE_PATH + ReviewTestData.REVIEW_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted review with id " + ReviewTestData.REVIEW_ID))
                .andReturn().getResponse().getContentAsString();
    }

    private String performRequestAndExpectSuccess(boolean isPost, String path, ReviewRequestDTO reviewRequestDTO)
            throws Exception {
        return mockMvc.perform((isPost ? post(path) : put(path))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(isPost ? status().isCreated() : status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }
}