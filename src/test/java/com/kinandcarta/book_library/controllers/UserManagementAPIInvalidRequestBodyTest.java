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
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(fullName,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.fullName",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_fullNameIsEmpty_returnsBadRequest(String fullName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(fullName,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.fullName",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_fullNameIsNull_returnsBadRequest(String fullName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(fullName,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.fullName",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_emailIsInvalid_returnsBadRequest(String email) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                email, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.email",
                ErrorMessages.EMAIL_BAD_FORMAT);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_emailIsEmpty_returnsBadRequest(String email) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                email, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.email",
                ErrorMessages.EMAIL_BAD_FORMAT);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_emailIsNull_returnsBadRequest(String email) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                email, SharedServiceTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.email",
                ErrorMessages.MUST_NOT_BE_NULL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_officeNameIsInvalid_returnsBadRequest(String officeName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, officeName, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.officeName",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_officeNameIsEmpty_returnsBadRequest(String officeName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, officeName, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.officeName",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_officeNameIsNull_returnsBadRequest(String officeName) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, officeName, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.officeName",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void registerUser_passwordIsInvalid_returnsBadRequest(String password) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, password);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.password",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void registerUser_passwordIsEmpty_returnsBadRequest(String password) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, password);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.password",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void registerUser_passwordIsNull_returnsBadRequest(String password) {
        // given
        final String registerUserPath = USERS_PATH + "/register";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(UserTestData.USER_FULL_NAME,
                UserTestData.USER_EMAIL, SharedServiceTestData.SKOPJE_OFFICE_NAME, password);

        // when & then
        performPostAndExpectBadRequest(registerUserPath, registrationRequestDTO, "$.errorFields.password",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void loginUser_userEmailIsInvalid_returnsBadRequest(String userEmail) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userEmail, UserTestData.USER_PASSWORD);

        // when & then
        performPostAndExpectBadRequest(loginUserPath, userLoginRequestDTO, "$.errorFields.userEmail",
                ErrorMessages.EMAIL_BAD_FORMAT);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void loginUser_userEmailIsEmpty_returnsBadRequest(String userEmail) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userEmail, UserTestData.USER_PASSWORD);

        // when && then
        performPostAndExpectBadRequest(loginUserPath, userLoginRequestDTO, "$.errorFields.userEmail",
                ErrorMessages.EMAIL_BAD_FORMAT);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void loginUser_userEmailIsNull_returnsBadRequest(String userEmail) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(userEmail, UserTestData.USER_PASSWORD);

        // when && then
        performPostAndExpectBadRequest(loginUserPath, userLoginRequestDTO, "$.errorFields.userEmail",
                ErrorMessages.MUST_NOT_BE_NULL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void loginUser_passwordIsInvalid_returnsBadRequest(String userPassword) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(UserTestData.USER_EMAIL, userPassword);

        // when & then
        performPostAndExpectBadRequest(loginUserPath, userLoginRequestDTO, "$.errorFields.userPassword",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void loginUser_passwordIsEmpty_returnsBadRequest(String userPassword) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(UserTestData.USER_EMAIL, userPassword);

        // when & then
        performPostAndExpectBadRequest(loginUserPath, userLoginRequestDTO, "$.errorFields.userPassword",
                ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void loginUser_passwordIsNull_returnsBadRequest(String userPassword) {
        // given
        final String loginUserPath = USERS_PATH + "/login";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(UserTestData.USER_EMAIL, userPassword);

        // when & then
        performPostAndExpectBadRequest(loginUserPath, userLoginRequestDTO, "$.errorFields.userPassword",
                ErrorMessages.MUST_NOT_BE_BLANK);
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
        performPatchAndExpectBadRequest(updateDataUserPath, userUpdateDataRequestDTO, "$.errorFields.userId",
                ErrorMessages.MUST_NOT_BE_NULL);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void updateUserRole_userIdIsNull_returnsBadRequest(UUID userId) {
        // given
        final String updateRoleUserPath = USERS_PATH + "/update-role";
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = new UserUpdateRoleRequestDTO(userId, UserRole.USER);

        // when & then
        performPatchAndExpectBadRequest(updateRoleUserPath, userUpdateRoleRequestDTO, "$.errorFields.userId",
                ErrorMessages.MUST_NOT_BE_NULL);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void updateUserRole_roleIsNull_returnsBadRequest(UserRole role) {
        // given
        final String updateRoleUserPath = USERS_PATH + "/update-role";
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = new UserUpdateRoleRequestDTO(UserTestData.USER_ID, role);

        // when & then
        performPatchAndExpectBadRequest(updateRoleUserPath, userUpdateRoleRequestDTO, "$.errorFields.role",
                ErrorMessages.MUST_NOT_BE_NULL);
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
        performPatchAndExpectBadRequest(changePasswordUserPath, userChangePasswordRequestDTO, "$.errorFields.userId",
                ErrorMessages.MUST_NOT_BE_NULL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void changeUserPassword_oldPasswordIsInvalid_returnsBadRequest(String oldPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, oldPassword, UserTestData.USER_PASSWORD);

        // when & then
        performPatchAndExpectBadRequest(changePasswordUserPath, userChangePasswordRequestDTO,
                "$.errorFields.oldPassword", ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void changeUserPassword_oldPasswordIsEmpty_returnsBadRequest(String oldPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, oldPassword, UserTestData.USER_PASSWORD);

        // when & then
        performPatchAndExpectBadRequest(changePasswordUserPath, userChangePasswordRequestDTO,
                "$.errorFields.oldPassword", ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_oldPasswordIsNull_returnsBadRequest(String oldPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, oldPassword, UserTestData.USER_PASSWORD);

        // when & then
        performPatchAndExpectBadRequest(changePasswordUserPath, userChangePasswordRequestDTO,
                "$.errorFields.oldPassword", ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t", "\n"})
    @SneakyThrows
    void changeUserPassword_newPasswordIsInvalid_returnsBadRequest(String newPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, UserTestData.USER_PASSWORD, newPassword);

        // when & then
        performPatchAndExpectBadRequest(changePasswordUserPath, userChangePasswordRequestDTO,
                "$.errorFields.newPassword", ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void changeUserPassword_newPasswordIsEmpty_returnsBadRequest(String newPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, UserTestData.USER_PASSWORD, newPassword);

        // when & then
        performPatchAndExpectBadRequest(changePasswordUserPath, userChangePasswordRequestDTO,
                "$.errorFields.newPassword", ErrorMessages.MUST_NOT_BE_BLANK);
    }

    @ParameterizedTest
    @NullSource
    @SneakyThrows
    void changeUserPassword_newPasswordIsNull_returnsBadRequest(String newPassword) {
        // given
        final String changePasswordUserPath = USERS_PATH + "/change-password";
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                new UserChangePasswordRequestDTO(UserTestData.USER_ID, UserTestData.USER_PASSWORD, newPassword);

        // when & then
        performPatchAndExpectBadRequest(changePasswordUserPath, userChangePasswordRequestDTO,
                "$.errorFields.newPassword", ErrorMessages.MUST_NOT_BE_BLANK);
    }

    private void performPostAndExpectBadRequest(String path, Record DTO, String errorField, String errorMessage)
            throws Exception {
         mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }

    private void performPatchAndExpectBadRequest(String path, Record DTO, String errorField, String errorMessage)
            throws Exception {
         mockMvc.perform(patch(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(errorField).value(errorMessage));
    }
}
