package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewManagementAPIInvalidRequestBodyTest {
    public static final String REVIEW_BASE_PATH = "/reviews";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_bookIsbnIsEmpty_returnsBadRequest(String invalidIsbn) {
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
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_userEmailIsEmpty_returnsBadRequest(String invalidEmail) {
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
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_messageIsEmpty_returnsBadRequest(String invalidMessage) {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", invalidMessage
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value("must not be blank"));
    }

    @ParameterizedTest
    @NullAndEmptySource
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

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_bookIsbnIsEmpty_returnsBadRequest(String invalidIsbn) {
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
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_userEmailIsEmpty_returnsBadRequest(String invalidEmail) {
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
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_messageIsEmpty_returnsBadRequest(String invalidMessage) {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", invalidMessage
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value("must not be blank"));
    }

    @ParameterizedTest
    @NullAndEmptySource
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
}
