package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.enums.UserRole;
import com.kinandcarta.book_library.services.impl.UserManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.ErrorMessages;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserManagementAPIInvalidRequestBodyTest {
    private static final String USERS_PATH = "/users";

    @MockBean
    private UserQueryServiceImpl userQueryService;

    @MockBean
    private UserManagementServiceImpl userManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_fullNameIsInvalid_returnsBadRequest(String fullName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingFullName(fullName);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.fullName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_fullNameIsEmpty_returnsBadRequest(String fullName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingFullName(fullName);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.fullName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_fullNameIsNull_returnsBadRequest(String fullName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingFullName(fullName);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.fullName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_emailIsInvalid_returnsBadRequest(String email) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingEmail(email);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.email").value(ErrorMessages.EMAIL_BAD_FORMAT));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_emailIsEmpty_returnsBadRequest(String email) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingEmail(email);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.email").value(ErrorMessages.EMAIL_BAD_FORMAT));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_emailIsNull_returnsBadRequest(String email) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingEmail(email);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.email").value(ErrorMessages.MUST_NOT_BE_NULL));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_officeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingOfficeName(officeName);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_officeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingOfficeName(officeName);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_officeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingOfficeName(officeName);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.officeName").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_passwordIsInvalid_returnsBadRequest(String password) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingPassword(password);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.password").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_passwordIsEmpty_returnsBadRequest(String password) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingPassword(password);
        ;

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.password").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_passwordIsNull_returnsBadRequest(String password) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO =
                UserTestData.createUserRegistrationRequestPassingPassword(password);

        // when & then
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.password").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void loginUser_userEmailIsInvalid_returnsBadRequest(String userEmail) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = UserTestData.createUserLoginRequestDTOPassingUserEmail(userEmail);

        // when & then
        mockMvc.perform(post(loginUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.EMAIL_BAD_FORMAT));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void loginUser_userEmailIsEmpty_returnsBadRequest(String userEmail) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = UserTestData.createUserLoginRequestDTOPassingUserEmail(userEmail);

        // when && then
        mockMvc.perform(post(loginUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.EMAIL_BAD_FORMAT));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void loginUser_userEmailIsNull_returnsBadRequest(String userEmail) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = UserTestData.createUserLoginRequestDTOPassingUserEmail(userEmail);

        // when && then
        mockMvc.perform(post(loginUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userEmail").value(ErrorMessages.MUST_NOT_BE_NULL));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void loginUser_passwordIsInvalid_returnsBadRequest(String userPassword) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO =
                UserTestData.createUserLoginRequestDTOPassingUserPassword(userPassword);

        // when & then
        mockMvc.perform(post(loginUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void loginUser_passwordIsEmpty_returnsBadRequest(String userPassword) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO =
                UserTestData.createUserLoginRequestDTOPassingUserPassword(userPassword);

        // when & then
        mockMvc.perform(post(loginUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void loginUser_passwordIsNull_returnsBadRequest(String userPassword) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO =
                UserTestData.createUserLoginRequestDTOPassingUserPassword(userPassword);

        // when & then
        mockMvc.perform(post(loginUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void updateUserData_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        final String updateDataUserPath = USERS_PATH + "/update-data";
        UserUpdateDataRequestDTO userUpdateDataRequestDTO = new UserUpdateDataRequestDTO(userId,
                UserTestData.USER_FULL_NAME, null);

        // when & then
        mockMvc.perform(patch(updateDataUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDataRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userId").value(ErrorMessages.MUST_NOT_BE_NULL));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void updateUserRole_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        final String updateRoleUserPath = USERS_PATH + "/update-role";
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = new UserUpdateRoleRequestDTO(userId, UserRole.USER);

        // when & then
        mockMvc.perform(patch(updateRoleUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRoleRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userId").value(ErrorMessages.MUST_NOT_BE_NULL));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void updateUserRole_roleIsNull_returnsBadRequest(UserRole role) {
        // given
        final String updateRoleUserPath = USERS_PATH + "/update-role";
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = new UserUpdateRoleRequestDTO(UserTestData.USER_ID, role);

        // when & then
        mockMvc.perform(patch(updateRoleUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRoleRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.role").value(ErrorMessages.MUST_NOT_BE_NULL));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(userId, UserTestData.USER_PASSWORD, UserTestData.USER_PASSWORD);

        // when & then
        mockMvc.perform(patch(changePasswordUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.userId").value(ErrorMessages.MUST_NOT_BE_NULL));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void changeUserPassword_oldPasswordIsInvalid_returnsBadRequest(String oldPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.createUserUserChangePasswordRequestDTOPassingOldPassword(oldPassword);

        // when & then
        mockMvc.perform(patch(changePasswordUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.oldPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void changeUserPassword_oldPasswordIsEmpty_returnsBadRequest(String oldPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.createUserUserChangePasswordRequestDTOPassingOldPassword(oldPassword);

        // when & then
        mockMvc.perform(patch(changePasswordUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.oldPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_oldPasswordIsNull_returnsBadRequest(String oldPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.createUserUserChangePasswordRequestDTOPassingOldPassword(oldPassword);

        // when & then
        mockMvc.perform(patch(changePasswordUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.oldPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void changeUserPassword_newPasswordIsInvalid_returnsBadRequest(String newPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.createUserUserChangePasswordRequestDTOPassingNewPassword(newPassword);

        // when & then
        mockMvc.perform(patch(changePasswordUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.newPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void changeUserPassword_newPasswordIsEmpty_returnsBadRequest(String newPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.createUserUserChangePasswordRequestDTOPassingNewPassword(newPassword);

        // when & then
        mockMvc.perform(patch(changePasswordUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.newPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_newPasswordIsNull_returnsBadRequest(String newPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.createUserUserChangePasswordRequestDTOPassingNewPassword(newPassword);

        // when & then
        mockMvc.perform(patch(changePasswordUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangePasswordRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorFields.newPassword").value(ErrorMessages.MUST_NOT_BE_BLANK));
    }
}
