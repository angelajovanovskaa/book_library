package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.services.impl.UserManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

@WebMvcTest(UserController.class)
class UserQueryAPIInvalidQueryParamsTest {
    private static final String USERS_PATH = "/users";
    private static final String USER_PATH_BY_FULL_NAME = USERS_PATH + "/by-full-name";
    private static final String USER_PATH_PROFILE = USERS_PATH + "/profile";

    private static final String ERROR_FIELD_DETAIL = "$.detail";
    private static final String ERROR_FIELD_GET_USERS_OFFICE_NAME = "$.errorFields['getUsers.officeName']";
    private static final String ERROR_FIELD_GET_USER_BY_FULL_NAME_OFFICE_NAME =
            "$.errorFields['getUsersByFullName.officeName']";

    @MockBean
    private UserQueryServiceImpl userService;

    @MockBean
    private UserManagementServiceImpl userManagementService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getUsers_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(USERS_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_USERS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK, MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getUsers_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(USERS_PATH, SharedControllerTestData.OFFICE_PARAM, officeName,
                ERROR_FIELD_GET_USERS_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK, MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUsers_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given & when & then
        performGetAndExpectBadRequest(USERS_PATH, SharedControllerTestData.OFFICE_PARAM, officeName, ERROR_FIELD_DETAIL,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getUsersByFullName_paramOfficeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                UserTestData.getUsersByFullNameQueryParamsPassingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(USER_PATH_BY_FULL_NAME, queryParamsValues,
                ERROR_FIELD_GET_USER_BY_FULL_NAME_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void getUsersByFullName_paramOfficeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                UserTestData.getUsersByFullNameQueryParamsPassingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(USER_PATH_BY_FULL_NAME, queryParamsValues,
                ERROR_FIELD_GET_USER_BY_FULL_NAME_OFFICE_NAME, ErrorMessages.MUST_NOT_BE_BLANK,
                MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUsersByFullName_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> queryParamsValues =
                UserTestData.getUsersByFullNameQueryParamsPassingOfficeName(officeName);

        // when & then
        performGetAndExpectBadRequest(USER_PATH_BY_FULL_NAME, queryParamsValues, ERROR_FIELD_DETAIL,
                ErrorMessages.OFFICE_NAME_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUsersByFullName_paramFullNameIsNull_returnsBadRequest(String fullName) {
        // given
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.FULL_NAME_PARAM, fullName);

        // when & then
        performGetAndExpectBadRequest(USER_PATH_BY_FULL_NAME, queryParamsValues, ERROR_FIELD_DETAIL,
                ErrorMessages.FULL_NAME_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    @SneakyThrows
    void getUserProfile_paramUserIdIsInvalid_returnsBadRequest() {
        // given
        String userIdInput = "wrongUserIdInput";

        // when & then
        performGetAndExpectBadRequest(USER_PATH_PROFILE, SharedControllerTestData.USER_ID_PARAM, userIdInput,
                ERROR_FIELD_DETAIL, String.format(ErrorMessages.USER_ID_FAIL_CONVERT, userIdInput),
                MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUserProfile_paramUserIdIsNull_returnsBadRequest(String userId) {
        // given & when & then
        performGetAndExpectBadRequest(USER_PATH_PROFILE, SharedControllerTestData.USER_ID_PARAM, userId,
                ERROR_FIELD_DETAIL, ErrorMessages.USER_ID_NOT_PRESENT, MediaType.APPLICATION_PROBLEM_JSON);
    }

    private void performGetAndExpectBadRequest(String path, String param, String paramValue, String errorField,
                                               String errorMessage, MediaType mediaType) throws Exception {
        mockMvc.perform(get(path).queryParam(param, paramValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(mediaType))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }

    private void performGetAndExpectBadRequest(String path, MultiValueMap<String, String> queryParamsValues,
                                               String errorField, String errorMessage, MediaType mediaType)
            throws Exception {
        mockMvc.perform(get(path).queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(mediaType))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}