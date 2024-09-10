package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookCheckoutQueryAPIInvalidQueryParamsTest {
    private static final String BOOK_CHECKOUTS_PATH = "/book-checkouts";
    private static final String BOOK_CHECKOUTS_PATH_PAGINATED = BOOK_CHECKOUTS_PATH + "/paginated";
    private static final String BOOK_CHECKOUTS_PATH_ACTIVE = BOOK_CHECKOUTS_PATH + "/active";
    private static final String BOOK_CHECKOUTS_PATH_PAST = BOOK_CHECKOUTS_PATH + "/past";
    private static final String BOOK_CHECKOUTS_PATH_NEAR_RETURN = BOOK_CHECKOUTS_PATH + "/near-return-date";
    private static final String BOOK_CHECKOUTS_PATH_BY_TITLE = BOOK_CHECKOUTS_PATH + "/by-title";
    private static final String ERROR_FIELD_DETAIL = "$.detail";

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH, SharedControllerTestData.OFFICE_PARAM,
                officeName, "$.errorFields['getBookCheckouts.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameIsEmpty_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH, SharedControllerTestData.OFFICE_PARAM,
                "", "$.errorFields['getBookCheckouts.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH, SharedControllerTestData.OFFICE_PARAM, null,
                ERROR_FIELD_DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_PAGINATED, SharedControllerTestData.OFFICE_PARAM,
                officeName, "$.errorFields['getBookCheckoutsPaginated.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameIsEmpty_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_PAGINATED, SharedControllerTestData.OFFICE_PARAM,
                "", "$.errorFields['getBookCheckoutsPaginated.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_PAGINATED, SharedControllerTestData.OFFICE_PARAM, null,
                ERROR_FIELD_DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_ACTIVE, SharedControllerTestData.OFFICE_PARAM,
                officeName, "$.errorFields['getActiveBookCheckouts.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameIsEmpty_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_ACTIVE, SharedControllerTestData.OFFICE_PARAM,
                "", "$.errorFields['getActiveBookCheckouts.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_ACTIVE, SharedControllerTestData.OFFICE_PARAM, null,
                ERROR_FIELD_DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_PAST, SharedControllerTestData.OFFICE_PARAM,
                officeName, "$.errorFields['getPastBookCheckouts.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameIsEmpty_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_PAST, SharedControllerTestData.OFFICE_PARAM,
                "", "$.errorFields['getPastBookCheckouts.officeName']", ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_PAST, SharedControllerTestData.OFFICE_PARAM, null,
                ERROR_FIELD_DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_NEAR_RETURN, SharedControllerTestData.OFFICE_PARAM,
                officeName, "$.errorFields['getBookCheckoutsNearReturnDate.officeName']",
                ErrorMessages.MUST_NOT_BE_BLANK, MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameIsEmpty_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_NEAR_RETURN, SharedControllerTestData.OFFICE_PARAM,
                "", "$.errorFields['getBookCheckoutsNearReturnDate.officeName']",
                ErrorMessages.MUST_NOT_BE_BLANK, MediaType.APPLICATION_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_NEAR_RETURN, SharedControllerTestData.OFFICE_PARAM,
                null, ERROR_FIELD_DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT,
                MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, "");

        // when & then
        performGetAndExpectBadRequest(queryParamsValues);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameIsEmpty_returnsBadRequest() {
        // given
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, "");
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, "");

        // when & then
        performGetAndExpectBadRequest(queryParamsValues);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameIsNull_returnsBadRequest() {
        // given & when & then
        performGetAndExpectBadRequest(BOOK_CHECKOUTS_PATH_BY_TITLE, SharedControllerTestData.OFFICE_PARAM,
                null, ERROR_FIELD_DETAIL, ErrorMessages.OFFICE_NAME_NOT_PRESENT,
                MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUser_ParamUserIdMissingOrEmpty_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/by-user";

        // when & then
        performGetAndExpectBadRequest(getUsersBookCheckoutPath, SharedControllerTestData.USER_ID_PARAM, null,
                ERROR_FIELD_DETAIL, ErrorMessages.USER_ID_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUserAndTitleContaining_ParamUserIdMissingOrEmpty_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutByTitlePath = BOOK_CHECKOUTS_PATH + "/by-user-and-title";

        // when & then
        performGetAndExpectBadRequest(getUsersBookCheckoutByTitlePath, SharedControllerTestData.USER_ID_PARAM, null,
                ERROR_FIELD_DETAIL, ErrorMessages.USER_ID_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    private void performGetAndExpectBadRequest(String path, String param, String paramValue, String errorField,
                                               String errorMessage, MediaType mediaType) throws Exception {
        mockMvc.perform(get(path).queryParam(param, paramValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(mediaType))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }

    private void performGetAndExpectBadRequest(MultiValueMap<String, String> queryParamsValues) throws Exception {
        mockMvc.perform(get(BOOK_CHECKOUTS_PATH_BY_TITLE).queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields['getBookCheckoutsByTitleContaining.officeName']").value(
                        ErrorMessages.MUST_NOT_BE_BLANK));
    }
}
