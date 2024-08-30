package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.ErrorMessages;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
                .andExpect(jsonPath("$.errorFields.bookISBN").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void insertReview_bookIsbnIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("", "email", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.bookISBN").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void insertReview_bookIsbnIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO(null, "email", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.bookISBN").value(ErrorMessages.MUST_NOT_BE_BLANK));
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
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void insertReview_userEmailIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void insertReview_userEmailIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", null, "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.MUST_NOT_BE_BLANK));
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
                .andExpect(jsonPath("$.errorFields.message").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void insertReview_messageIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", ""
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void insertReview_messageIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(post(REVIEW_BASE_PATH + "/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", null
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value(ErrorMessages.MUST_NOT_BE_BLANK));
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
                .andExpect(jsonPath("$.errorFields.rating").value(ErrorMessages.MUST_BE_GREATER_THAN_1));
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
                .andExpect(jsonPath("$.errorFields.bookISBN").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void updateReview_bookIsbnIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("", "email", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.bookISBN").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void updateReview_bookIsbnIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO(null, "email", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.bookISBN").value(ErrorMessages.MUST_NOT_BE_BLANK));
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
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void updateReview_userEmailIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "", "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void updateReview_userEmailIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", null, "message"
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.MUST_NOT_BE_BLANK));
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
                .andExpect(jsonPath("$.errorFields.message").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void updateReview_messageIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", ""
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @Test
    @SneakyThrows
    void updateReview_messageIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(put(REVIEW_BASE_PATH + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", null
                                , 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields.message").value(ErrorMessages.MUST_NOT_BE_BLANK));
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
                .andExpect(jsonPath("$.errorFields.rating").value(ErrorMessages.MUST_BE_GREATER_THAN_1));
    }
}
