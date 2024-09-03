package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.config.JwtService;
import com.kinandcarta.book_library.services.impl.UserManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserQueryAPIQueryParamsMissingTest {
    private static final String USERS_PATH = "/users";

    private static final String ERROR_FIELD_DETAIL = "$.detail";

    @MockBean
    private UserQueryServiceImpl userService;

    @MockBean
    private UserManagementServiceImpl userManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getUsers_paramOfficeNameIsMissing_returnsBadRequest() {
        // given & when & then
        performGetRequestAndExpectBadRequest(USERS_PATH, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getUsersByFullName_paramOfficeNameIsMissing_returnsBadRequest() {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";

        // when & then
        performGetRequestAndExpectBadRequest(getUsersByFullNamePath, ErrorMessages.OFFICE_NAME_NOT_PRESENT);
    }

    @Test
    @SneakyThrows
    void getUsersByFullName_paramFullNameIsMissing_returnsBadRequest() {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";

        // when & then
        mockMvc.perform(get(getUsersByFullNamePath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(ErrorMessages.FULL_NAME_NOT_PRESENT));
    }

    @Test
    @SneakyThrows
    void getUserProfile_paramUserIdIsMissing_returnsBadRequest() {
        // given
        final String getUserProfilePath = USERS_PATH + "/profile";

        // when & then
        performGetRequestAndExpectBadRequest(getUserProfilePath, ErrorMessages.USER_ID_NOT_PRESENT);
    }

    private void performGetRequestAndExpectBadRequest(String path, String errorMessage)
            throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(ERROR_FIELD_DETAIL).value(errorMessage));
    }
}
