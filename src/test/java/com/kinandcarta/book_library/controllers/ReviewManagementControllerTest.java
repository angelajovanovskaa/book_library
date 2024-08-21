package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_bookIsbnIsNullOrEmpty_returnsBadRequest(String invalidIsbn) {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO(invalidIsbn, "email", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.bookISBN").value("must not be blank"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_userEmailIsNullOrEmpty_returnsBadRequest(String invalidEmail) {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", invalidEmail, "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.userEmail").value("must not be blank"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_messageIsNullOrEmpty_returnsBadRequest(String invalidMessage) {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", invalidMessage
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value("must not be blank"));
    }

    @Test
    @SneakyThrows
    void insertReview_ratingIsOutOfBounds_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookISBN\": \"isbn\", \"userEmail\": \"email\", \"message\": \"message\", " +
                                "\"rating\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.rating").value("must be greater than or equal to 1"));
    }

    @Test
    @SneakyThrows
    void insertReview_userNotFound_returnsBadRequest() {
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
    void insertReview_bookNotFound_returnsBadRequest() {
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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_bookIsbnIsNullOrEmpty_returnsBadRequest(String invalidIsbn) {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO(invalidIsbn, "email", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.bookISBN").value("must not be blank"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_userEmailIsNullOrEmpty_returnsBadRequest(String invalidEmail) {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", invalidEmail, "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.userEmail").value("must not be blank"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_messageIsNullOrEmpty_returnsBadRequest(String invalidMessage) {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", invalidMessage
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value("must not be blank"));
    }

    @Test
    @SneakyThrows
    void updateReview_ratingIsOutOfBounds_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookISBN\": \"isbn\", \"userEmail\": \"email\", \"message\": \"message\", " +
                                "\"rating\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.rating").value("must be greater than or equal to 1"));
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

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void deleteReview_reviewIdIsNotValid_returnsBadRequest(String reviewId) {
        // given

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/" + reviewId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required path variable 'reviewId' is not present."));
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
    void deleteReview_reviewIdIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(delete(REVIEW_BASE_PATH + "/delete/" + null))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.detail").value("Failed to convert 'reviewId' with value: 'null'"));
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