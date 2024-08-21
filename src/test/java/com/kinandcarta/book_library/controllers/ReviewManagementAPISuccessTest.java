package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.ReviewTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
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
public class ReviewManagementAPISuccessTest {
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
    void insertReview_insertIsValid_returnsReviewResponseDTO() {
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

    @Test
    @SneakyThrows
    void updateReview_validRequest_returnsUpdatedReviewResponseDTO() {
        // given
        given(reviewManagementService.updateReview(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        String jsonResult = mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ReviewTestData.getReviewRequestDTO())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        ReviewResponseDTO actualResponse = objectMapper.readValue(jsonResult, ReviewResponseDTO.class);

        // then
        assertThat(actualResponse).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void deleteReview_validReviewId_returnsSuccessMessage() {
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
}
