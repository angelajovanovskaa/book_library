package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewQueryAPIQueryParamsMissingTest {
    private static final String REVIEW_BASE_PATH = "/reviews";
    private static final String TOP_REVIEWS_PATH = REVIEW_BASE_PATH + "/top-reviews";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsMissing_returnsBadRequest() {
        // when & then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        performRequestAndExpectBadRequest(REVIEW_BASE_PATH, params, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_isbnParamIsMissing_returnsBadRequest() {
        // when & then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        performRequestAndExpectBadRequest(REVIEW_BASE_PATH, params, ErrorMessages.ISBN_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsMissing_returnsBadRequest() {
        // when & then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsMissing_returnsBadRequest() {
        // when & then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params, ErrorMessages.ISBN_NOT_PRESENT);
    }

    private void performRequestAndExpectBadRequest(String path, MultiValueMap<String, String> params,
                                                   String errorMessage) throws Exception {
        mockMvc.perform(get(path).queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }
}
