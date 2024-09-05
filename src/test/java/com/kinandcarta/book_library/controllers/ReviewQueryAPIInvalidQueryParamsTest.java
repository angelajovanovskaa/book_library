package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewQueryAPIInvalidQueryParamsTest {
    private static final String REVIEW_BASE_PATH = "/reviews";
    private static final String TOP_REVIEWS_PATH = REVIEW_BASE_PATH + "/top-reviews";
    private static final String ERROR_FIELD_TEMPLATE_REVIEWS_FOR_BOOK = "$.errorFields['getReviewsForBook.%s']";
    private static final String ERROR_FIELD_TEMPLATE_TOP_REVIEWS = "$.errorFields['getTopReviewsForBook.%s']";
    private static final String DETAIL_JSON_PATH = "$.detail";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsBlank_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        performRequestAndExpectBadRequest(REVIEW_BASE_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_REVIEWS_FOR_BOOK, SharedControllerTestData.OFFICE_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, "");
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        performRequestAndExpectBadRequest(REVIEW_BASE_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_REVIEWS_FOR_BOOK, SharedControllerTestData.OFFICE_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, null);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(ErrorMessages.OFFICE_NAME_NOT_PRESENT));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getReviewsForBook_isbnParamIsBlank_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, isbn);

        // when & then
        performRequestAndExpectBadRequest(REVIEW_BASE_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_REVIEWS_FOR_BOOK, SharedControllerTestData.BOOK_ISBN_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_isbnParamIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, "");

        // when & then
        performRequestAndExpectBadRequest(REVIEW_BASE_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_REVIEWS_FOR_BOOK, SharedControllerTestData.BOOK_ISBN_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_isbnParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, null);

        // when & then
        performRequestAndExpectBadRequest(REVIEW_BASE_PATH, params, DETAIL_JSON_PATH, ErrorMessages.ISBN_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsBlank_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_TOP_REVIEWS, SharedControllerTestData.OFFICE_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, "");
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_TOP_REVIEWS, SharedControllerTestData.OFFICE_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, null);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params, DETAIL_JSON_PATH,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsBlank_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, isbn);

        // when & then
        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_TOP_REVIEWS, SharedControllerTestData.BOOK_ISBN_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, "");

        // when & then
        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params,
                String.format(ERROR_FIELD_TEMPLATE_TOP_REVIEWS, SharedControllerTestData.BOOK_ISBN_PARAM),
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, null);

        // when & then
        performRequestAndExpectBadRequest(TOP_REVIEWS_PATH, params, DETAIL_JSON_PATH, ErrorMessages.ISBN_NOT_PRESENT);
    }

    private void performRequestAndExpectBadRequest(String path, MultiValueMap<String, String> params,
                                                   String errorField, String errorMessage) throws Exception {
        mockMvc.perform(get(path).queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}