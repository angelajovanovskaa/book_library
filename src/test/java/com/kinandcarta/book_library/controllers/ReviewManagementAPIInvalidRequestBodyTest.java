package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.config.JwtService;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.ErrorMessages;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewManagementAPIInvalidRequestBodyTest {
    private static final String REVIEW_BASE_PATH = "/reviews";
    private static final String INSERT_REVIEW_PATH = REVIEW_BASE_PATH + "/insert";
    private static final String UPDATE_REVIEW_PATH = REVIEW_BASE_PATH + "/update";
    private static final String ERROR_FIELD_RATING = "$.errorFields.rating";
    private static final String ERROR_FIELD_MESSAGE = "$.errorFields.message";
    private static final String ERROR_FIELD_EMAIL = "$.errorFields.userEmail";
    private static final String ERROR_FIELD_ISBN = "$.errorFields.bookISBN";
    private static final String REVIEW_REQUEST_DTO_WITH_RATING_OUT_OF_BOUNDS =
            "{\"bookISBN\": \"isbn\", \"userEmail\": \"email\", \"message\": \"message\", \"rating\": 0}";

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

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_bookIsbnIsBlank_returnsBadRequest(String invalidIsbn) {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO(invalidIsbn, "email", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void insertReview_bookIsbnIsEmpty_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("", "email", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void insertReview_bookIsbnIsNull_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO(null, "email", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_userEmailIsBlank_returnsBadRequest(String invalidEmail) {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", invalidEmail, "message", 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_EMAIL,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void insertReview_userEmailIsEmpty_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_EMAIL,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void insertReview_userEmailIsNull_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", null, "message", 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_EMAIL,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void insertReview_messageIsBlank_returnsBadRequest(String invalidMessage) {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", invalidMessage, 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_MESSAGE,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void insertReview_messageIsEmpty_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", "", 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_MESSAGE,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void insertReview_messageIsNull_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", null, 5));

        // when & then
        performRequestAndExpectBadRequest(INSERT_REVIEW_PATH, requestBody, ERROR_FIELD_MESSAGE,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @Test
    @SneakyThrows
    void insertReview_ratingIsOutOfBounds_returnsBadRequest() {
        // given & when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, REVIEW_REQUEST_DTO_WITH_RATING_OUT_OF_BOUNDS,
                ERROR_FIELD_RATING,
                ErrorMessages.MUST_BE_GREATER_THAN_1, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_bookIsbnIsBlank_returnsBadRequest(String invalidIsbn) {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO(invalidIsbn, "email", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @Test
    @SneakyThrows
    void updateReview_bookIsbnIsEmpty_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("", "email", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @Test
    @SneakyThrows
    void updateReview_bookIsbnIsNull_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO(null, "email", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_ISBN,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_userEmailIsBlank_returnsBadRequest(String invalidEmail) {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", invalidEmail, "message", 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_EMAIL,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @Test
    @SneakyThrows
    void updateReview_userEmailIsEmpty_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "", "message", 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_EMAIL,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @Test
    @SneakyThrows
    void updateReview_userEmailIsNull_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", null, "message", 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_EMAIL,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void updateReview_messageIsBlank_returnsBadRequest(String invalidMessage) {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", invalidMessage, 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_MESSAGE,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @Test
    @SneakyThrows
    void updateReview_messageIsEmpty_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", "", 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_MESSAGE,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @Test
    @SneakyThrows
    void updateReview_messageIsNull_returnsBadRequest() {
        // given
        String requestBody = objectMapper.writeValueAsString(new ReviewRequestDTO("isbn", "email", null, 5));

        // when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, requestBody, ERROR_FIELD_MESSAGE,
                ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @Test
    @SneakyThrows
    void updateReview_ratingIsOutOfBounds_returnsBadRequest() {
        // given & when & then
        performRequestAndExpectBadRequest(UPDATE_REVIEW_PATH, REVIEW_REQUEST_DTO_WITH_RATING_OUT_OF_BOUNDS,
                ERROR_FIELD_RATING,
                ErrorMessages.MUST_BE_GREATER_THAN_1, false);
    }

    private void performRequestAndExpectBadRequest(String path, String requestBody, String errorField,
                                                   String errorMessage,
                                                   boolean isPost) throws Exception {
        mockMvc.perform((isPost ? post(path) : put(path))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}