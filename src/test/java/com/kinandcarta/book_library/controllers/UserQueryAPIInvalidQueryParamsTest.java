package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.impl.UserServiceImpl;
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

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getUsers_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(USERS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Validation failure"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUsers_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given & when & then
        mockMvc.perform(get(USERS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM, officeName))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void getUsersByFullName_paramOfficeNameIsBlank_returnsBadRequest(String officeName) {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(SharedControllerTestData.FULL_NAME_PARAM, UserTestData.USER_FULL_NAME);

        // when & then
        mockMvc.perform(get(getUsersByFullNamePath).queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Validation failure"));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUsersByFullName_paramOfficeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(SharedControllerTestData.FULL_NAME_PARAM, UserTestData.USER_FULL_NAME);

        // when & then
        mockMvc.perform(get(getUsersByFullNamePath).queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUsersByFullName_paramFullNameIsNull_returnsBadRequest(String fullName) {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.FULL_NAME_PARAM, fullName);

        // when & then
        mockMvc.perform(get(getUsersByFullNamePath).queryParams(queryParamsValues))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'fullName' is not present."));
    }

    @Test
    @SneakyThrows
    void getUserProfile_paramUserIdIsInvalid_returnsBadRequest() {
        // given
        final String getUserProfilePath = USERS_PATH + "/profile";
        String userIdInput = "ABCD";

        // when & then
        mockMvc.perform(get(getUserProfilePath).queryParam(SharedControllerTestData.USER_ID_PARAM, userIdInput))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(String.format("Failed to convert 'userId' with value: '%s'",
                        userIdInput)));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void getUserProfile_paramUserIdIsNull_returnsBadRequest(String userId) {
        // given
        final String getUserProfilePath = USERS_PATH + "/profile";

        // when & then
        mockMvc.perform(get(getUserProfilePath).queryParam(SharedControllerTestData.USER_ID_PARAM, userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'userId' is not present."));
    }
}
