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
import com.kinandcarta.book_library.utils.SharedServiceTestData;
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
    private static final String USERS_PATH_REGISTER = USERS_PATH + "/register";
    private static final String USERS_PATH_LOGIN = USERS_PATH + "/login";
    private static final String USERS_PATH_UPDATE_ROLE = USERS_PATH + "/update-role";
    private static final String USERS_PATH_CHANGE_PASSWORD = USERS_PATH + "/change-password";

    private static final String ERROR_FIELD_FULL_NAME = "$.errorFields.fullName";
    private static final String ERROR_FIELD_EMAIL = "$.errorFields.email";
    private static final String ERROR_FIELD_OFFICE_NAME = "$.errorFields.officeName";
    private static final String ERROR_FIELD_PASSWORD = "$.errorFields.password";
    private static final String ERROR_FIELD_USER_EMAIL = "$.errorFields.userEmail";
    private static final String ERROR_FIELD_USER_PASSWORD = "$.errorFields.userPassword";
    private static final String ERROR_FIELD_USER_ID = "$.errorFields.userId";
    private static final String ERROR_FIELD_OLD_PASSWORD = "$.errorFields.oldPassword";
    private static final String ERROR_FIELD_NEW_PASSWORD = "$.errorFields.newPassword";

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
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(fullName,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_FULL_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_fullNameIsEmpty_returnsBadRequest(String fullName) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(fullName,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_FULL_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_fullNameIsNull_returnsBadRequest(String fullName) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(fullName,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_FULL_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_emailIsInvalid_returnsBadRequest(String email) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                email, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_EMAIL,
                ErrorMessages.EMAIL_BAD_FORMAT, true);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_emailIsEmpty_returnsBadRequest(String email) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                email, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_EMAIL,
                ErrorMessages.EMAIL_BAD_FORMAT, true);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_emailIsNull_returnsBadRequest(String email) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                email, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_EMAIL,
                ErrorMessages.MUST_NOT_BE_NULL, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_officeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, officeName, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_OFFICE_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_officeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, officeName, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_OFFICE_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_officeNameIsNull_returnsBadRequest(String officeName) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, officeName, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_OFFICE_NAME,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_passwordIsInvalid_returnsBadRequest(String password) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, password);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_PASSWORD,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_passwordIsEmpty_returnsBadRequest(String password) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, password);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_PASSWORD,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_passwordIsNull_returnsBadRequest(String password) {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, password);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_REGISTER, registrationRequestDTO, ERROR_FIELD_PASSWORD,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void loginUser_userEmailIsInvalid_returnsBadRequest(String userEmail) {
        // given
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userEmail, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_LOGIN, userLoginRequestDTO, ERROR_FIELD_USER_EMAIL,
                ErrorMessages.EMAIL_BAD_FORMAT, true);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void loginUser_userEmailIsEmpty_returnsBadRequest(String userEmail) {
        // given
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userEmail, UserTestData.USER_PASSWORD);

        // when && then
        performRequestAndExpectBadRequest(USERS_PATH_LOGIN, userLoginRequestDTO, ERROR_FIELD_USER_EMAIL,
                ErrorMessages.EMAIL_BAD_FORMAT, true);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void loginUser_userEmailIsNull_returnsBadRequest(String userEmail) {
        // given
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userEmail, UserTestData.USER_PASSWORD);

        // when && then
        performRequestAndExpectBadRequest(USERS_PATH_LOGIN, userLoginRequestDTO, ERROR_FIELD_USER_EMAIL,
                ErrorMessages.MUST_NOT_BE_NULL, true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void loginUser_passwordIsInvalid_returnsBadRequest(String userPassword) {
        // given
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(UserTestData.USER_EMAIL, userPassword);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_LOGIN, userLoginRequestDTO, ERROR_FIELD_USER_PASSWORD,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void loginUser_passwordIsEmpty_returnsBadRequest(String userPassword) {
        // given
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(UserTestData.USER_EMAIL, userPassword);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_LOGIN, userLoginRequestDTO, ERROR_FIELD_USER_PASSWORD,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void loginUser_passwordIsNull_returnsBadRequest(String userPassword) {
        // given
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(UserTestData.USER_EMAIL, userPassword);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_LOGIN, userLoginRequestDTO, ERROR_FIELD_USER_PASSWORD,
                ErrorMessages.MUST_NOT_BE_BLANK, true);
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
        performRequestAndExpectBadRequest(updateDataUserPath, userUpdateDataRequestDTO, ERROR_FIELD_USER_ID,
                ErrorMessages.MUST_NOT_BE_NULL, false);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void updateUserRole_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = new UserUpdateRoleRequestDTO(userId, UserRole.USER);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_UPDATE_ROLE, userUpdateRoleRequestDTO, ERROR_FIELD_USER_ID,
                ErrorMessages.MUST_NOT_BE_NULL, false);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void updateUserRole_roleIsNull_returnsBadRequest(UserRole role) {
        // given
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = new UserUpdateRoleRequestDTO(UserTestData.USER_ID, role);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_UPDATE_ROLE, userUpdateRoleRequestDTO, "$.errorFields.role",
                ErrorMessages.MUST_NOT_BE_NULL, false);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(userId, UserTestData.USER_PASSWORD, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_CHANGE_PASSWORD, userChangePasswordRequestDTO, ERROR_FIELD_USER_ID,
                ErrorMessages.MUST_NOT_BE_NULL, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void changeUserPassword_oldPasswordIsInvalid_returnsBadRequest(String oldPassword) {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, oldPassword, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_CHANGE_PASSWORD, userChangePasswordRequestDTO,
                ERROR_FIELD_OLD_PASSWORD, ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void changeUserPassword_oldPasswordIsEmpty_returnsBadRequest(String oldPassword) {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, oldPassword, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_CHANGE_PASSWORD, userChangePasswordRequestDTO,
                ERROR_FIELD_OLD_PASSWORD, ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_oldPasswordIsNull_returnsBadRequest(String oldPassword) {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, oldPassword, UserTestData.USER_PASSWORD);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_CHANGE_PASSWORD, userChangePasswordRequestDTO,
                ERROR_FIELD_OLD_PASSWORD, ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void changeUserPassword_newPasswordIsInvalid_returnsBadRequest(String newPassword) {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, UserTestData.USER_PASSWORD, newPassword);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_CHANGE_PASSWORD, userChangePasswordRequestDTO,
                ERROR_FIELD_NEW_PASSWORD, ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void changeUserPassword_newPasswordIsEmpty_returnsBadRequest(String newPassword) {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, UserTestData.USER_PASSWORD, newPassword);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_CHANGE_PASSWORD, userChangePasswordRequestDTO,
                ERROR_FIELD_NEW_PASSWORD, ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_newPasswordIsNull_returnsBadRequest(String newPassword) {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, UserTestData.USER_PASSWORD, newPassword);

        // when & then
        performRequestAndExpectBadRequest(USERS_PATH_CHANGE_PASSWORD, userChangePasswordRequestDTO,
                ERROR_FIELD_NEW_PASSWORD, ErrorMessages.MUST_NOT_BE_BLANK, false);
    }

    private void performRequestAndExpectBadRequest(String path, Record DTO, String errorField, String errorMessage,
                                                   boolean isPost) throws Exception {
        mockMvc.perform((isPost ? post(path) : patch(path))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}