package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.services.impl.AuthenticationServiceImpl;
import com.kinandcarta.book_library.services.impl.UserManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.UserResponseMessages;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.kinandcarta.book_library.utils.UserTestData.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserManagementAPISuccessTest {
    private static final String USERS_PATH = "/users";
    private static final String MOCK_TOKEN = "mockedToken";
    private static final String BEARER_PREFIX = "Bearer ";

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @MockBean
    private UserManagementServiceImpl userManagementService;

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        given(jwtService.validateToken(eq(MOCK_TOKEN), any(UserDetails.class))).willReturn(true);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(UserTestData.USER_EMAIL, null, List.of()));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void registerUser_registrationIsSuccessful_returnsUserWithRoleDTO() throws Exception {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO userRegistrationRequestDTO = UserTestData.getUserRegistrationDTO();
        UserWithRoleDTO userWithRoleDTO = UserTestData.getUserWithRoleResponseDTOs().getFirst();

        given(userManagementService.registerUser(any())).willReturn(userWithRoleDTO);

        // when
        String jsonResult = mockMvc.perform(post(registerUserPath)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserWithRoleDTO result = objectMapper.readValue(jsonResult, UserWithRoleDTO.class);

        // then
        assertThat(result).isEqualTo(userWithRoleDTO);
    }

    @Test
    void authenticateAndGetToken_loginIsSuccessful_returnsToken() throws Exception {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = UserTestData.getUserLoginRequestDTO();

        given(authenticationService.generateToken(userLoginRequestDTO)).willReturn(MOCK_TOKEN);

        // when
        String result = mockMvc.perform(post(loginUserPath)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(result).isEqualTo(MOCK_TOKEN);
    }

    @Test
    void updateUserData_updateIsSuccessful_returnsConfirmationMessage() throws Exception {
        // given
        final String updateDataUserPath = USERS_PATH + "/update-data";
        UserUpdateDataRequestDTO userUpdateDataRequestDTO = UserTestData.getUserUpdateDataRequestDTO();

        given(userManagementService.updateUserData(any())).willReturn(UserResponseMessages.USER_DATA_UPDATED_RESPONSE);

        // when
        String result = performPatchAndExpectConfirmationMessage(updateDataUserPath, userUpdateDataRequestDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DATA_UPDATED_RESPONSE);
    }

    @Test
    void updateUserRole_updateIsSuccessful_returnsConfirmationMessage() throws Exception {
        // given
        final String updateRoleUserPath = USERS_PATH + "/update-role";
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = UserTestData.getUserUpdateRoleRequestDTO();

        given(userManagementService.updateUserRole(any())).willReturn(UserResponseMessages.USER_ROLE_UPDATED_RESPONSE);

        // when
        String result = performPatchAndExpectConfirmationMessage(updateRoleUserPath, userUpdateRoleRequestDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_ROLE_UPDATED_RESPONSE);
    }

    @Test
    void deleteUser_deleteIsSuccessful_returnsConfirmationMessage() throws Exception {
        // given
        final String deleteUserPath = USERS_PATH + "/delete/{userId}";

        given(userManagementService.deleteAccount(any())).willReturn(UserResponseMessages.USER_DELETED_RESPONSE);

        // when
        String result = mockMvc.perform(post(deleteUserPath, USER_ID)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + MOCK_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DELETED_RESPONSE);
    }

    @Test
    void changeUserPassword_changeIsSuccessful_returnsConfirmationMessage() throws Exception {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO = UserTestData.getUserChangePasswordRequestDTO();

        given(userManagementService.changeUserPassword(any())).willReturn(
                UserResponseMessages.USER_PASSWORD_UPDATED_RESPONSE);

        // when
        String result = performPatchAndExpectConfirmationMessage(changePasswordUserPath, userChangePasswordRequestDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_PASSWORD_UPDATED_RESPONSE);
    }

    private String performPatchAndExpectConfirmationMessage(String path, Record DTO) throws Exception {
        return mockMvc.perform(patch(path)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + MOCK_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }
}
