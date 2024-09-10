package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.jwt.JwtService;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewManagementAPIInvalidPathVariablesTest {
    private static final String REVIEW_BASE_PATH = "/reviews";
    private static final String REVIEW_DELETE_PATH = REVIEW_BASE_PATH + "/delete/";
    private static final String DETAIL_JSON_PATH = "$.detail";

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

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void deleteReview_reviewIdIsBlank_returnsBadRequest(String reviewId) {
        // given & when & then
        performDeleteAndExpectBadRequest(REVIEW_DELETE_PATH + reviewId,
                ErrorMessages.REVIEW_ID_PATH_VARIABLE_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void deleteReview_reviewIdIsNull_returnsBadRequest() {
        // given & when & then
        performDeleteAndExpectBadRequest(REVIEW_DELETE_PATH + null,
                ErrorMessages.REVIEW_ID_PATH_VARIABLE_FAILED_TO_CONVERT);
    }

    private void performDeleteAndExpectBadRequest(String path, String errorMessage) throws Exception {
        mockMvc.perform(delete(path))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(DETAIL_JSON_PATH).value(errorMessage));
    }
}