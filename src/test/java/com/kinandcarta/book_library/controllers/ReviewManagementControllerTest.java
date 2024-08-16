package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.utils.ReviewTestData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor
class ReviewManagementControllerTest {
    public static final String REVIEW_BASE_PATH = "/reviews";
    public static final String REVIEW_INSERT_PATH = REVIEW_BASE_PATH + "/insert";

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void insertNewReview_insertIsValid_returnReviewResponseDTO() throws Exception {
        // given
        given(reviewManagementService.insertReview(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        String jsonResult =
                mockMvc.perform(post(REVIEW_INSERT_PATH)
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
    void insertNewReview_bookISBNIsBlank_thenReturnsBadRequest(String invalidISBN) {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(invalidISBN, "email", "message", 5);

        // when & then
        mockMvc.perform(post("/reviews/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertNewReview_userEmailIsBlank_thenReturnsBadRequest(String invalidEmail) {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("isbn", invalidEmail, "message", 5);

        // when & then
        mockMvc.perform(post("/reviews/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertNewReview_messageIsBlank_thenReturnsBadRequest(String invalidMessage) {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("isbn", "email", invalidMessage, 5);

        // when & then
        mockMvc.perform(post("/reviews/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void insertNewReview_ratingDoesNotMeetRangeRequirements_thenReturnsBadRequest() {
        // given
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("isbn", "email", "message", 0);

        // when, then
        mockMvc.perform(post("/reviews/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isBadRequest());
    }
}