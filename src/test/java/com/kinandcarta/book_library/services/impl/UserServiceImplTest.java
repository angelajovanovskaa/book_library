package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.EmailAlreadyInUseException;
import com.kinandcarta.book_library.exceptions.IncorrectPasswordException;
import com.kinandcarta.book_library.exceptions.InvalidUserCredentialsException;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.utils.SharedTestData;
import com.kinandcarta.book_library.utils.UserResponseMessages;
import com.kinandcarta.book_library.utils.UserTestData;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_emailAlreadyExists_throwsEmailAlreadyInUseException() {
        // given
        UserRegistrationRequestDTO userRegistrationDTO = UserTestData.getUserRegistrationDTO();

        given(userRepository.findByEmail(anyString())).willReturn(
                Optional.of(UserTestData.getUser()));

        // when && then
        assertThatExceptionOfType(EmailAlreadyInUseException.class)
                .isThrownBy(() -> userService.registerUser(userRegistrationDTO))
                .withMessage("The email: " + UserTestData.USER_EMAIL + " is already in use.");
    }

    @Test
    void loginUser_thereIsNoUserWithTheCredentials_throwsInvalidUserCredentialsException() {
        // given
        UserLoginRequestDTO userLoginRequestDTO = UserTestData.getUserLoginRequestDTO();

        // when && then
        assertThatExceptionOfType(InvalidUserCredentialsException.class)
                .isThrownBy(() -> userService.loginUser(userLoginRequestDTO))
                .withMessage("The credentials that you have entered don't match.");
    }

    @Test
    void changeUserPassword_oldPasswordDoesNotMatch_throwsIncorrectPasswordException() {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.getUserChangePasswordRequestDTOInvalid();

        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when && then
        assertThatExceptionOfType(IncorrectPasswordException.class)
                .isThrownBy(() -> userService.changeUserPassword(userChangePasswordRequestDTO))
                .withMessage("The password that you have entered is incorrect.");
    }

    @Test
    void getAllUsers_theListHasAtLeastOne_returnsListOfUserWithRoleFieldResponseDTO() {
        // given
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOs = UserTestData.getUserWithRoleResponseDTOs();

        given(userRepository.findAllByOffice_NameOrderByRoleAsc(anyString())).willReturn(UserTestData.getUsers());
        given(userConverter.toUserWithRoleDTO(any())).willReturn(userWithRoleFieldResponseDTOs.get(0),
                userWithRoleFieldResponseDTOs.get(1));

        // when
        List<UserWithRoleFieldResponseDTO> result = userService.getAllUsers(SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(result).isEqualTo(userWithRoleFieldResponseDTOs);
    }

    @Test
    void getAllUsersWithFullName_HasMatchesWithSearchTerm_returnsListOfUserWithRoleFieldResponseDTO() {
        // given
        List<UserWithRoleFieldResponseDTO> userWithRoleResponseDTOs = UserTestData.getUserWithRoleResponseDTOs();

        given(userRepository.findByOffice_NameAndFullNameContainingIgnoreCaseOrderByRoleAsc(any(), any())).willReturn(
                UserTestData.getUsers());
        given(userConverter.toUserWithRoleDTO(any())).willReturn(userWithRoleResponseDTOs.get(0),
                userWithRoleResponseDTOs.get(1));

        // when
        List<UserWithRoleFieldResponseDTO> result =
                userService.getAllUsersWithFullName(SharedTestData.SKOPJE_OFFICE_NAME, UserTestData.USER_FULL_NAME);

        // then
        assertThat(result).isEqualTo(UserTestData.getUserWithRoleResponseDTOs());
    }

    @Test
    void getUserProfile_userExist_returnsUserWithoutRoleDTO() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());
        given(userConverter.toUserResponseDTO(any())).willReturn(UserTestData.getUserResponseDTO());

        // when
        UserResponseDTO result = userService.getUserProfile(UserTestData.USER_ID);

        // then
        assertThat(result).isEqualTo(UserTestData.getUserResponseDTO());
    }

    @Test
    void registerUser_theRegistrationIsSuccessful_returnsConfirmationMessage() throws IOException {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = UserTestData.getUserRegistrationDTO();

        given(userConverter.toUserEntity(registrationRequestDTO)).willReturn(new User());

        given(officeRepository.getReferenceById(anyString())).willReturn(SharedTestData.SKOPJE_OFFICE);

        Resource mockResource = mock(Resource.class);
        given(mockResource.getContentAsByteArray()).willReturn(UserTestData.USER_IMAGE_BYTES);
        given(resourceLoader.getResource(any())).willReturn(mockResource);

        // when
        String result = userService.registerUser(registrationRequestDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_REGISTERED_RESPONSE);
    }

    @Test
    void loginUser_loginIsValid_returnsConfirmationMessage() {
        // given
        given(userRepository.findByEmailAndPassword(anyString(), anyString())).willReturn(
                Optional.of(UserTestData.getUser()));

        // when
        String result = userService.loginUser(UserTestData.getUserLoginRequestDTO());

        // then
        assertThat(result).isEqualTo(UserTestData.USER_FULL_NAME);
    }

    @Test
    void updateUserData_updateUserProfilePicture_returnsConfirmationMessage() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when
        String result = userService.updateUserData(UserTestData.getUserUpdateDataRequestDTO());

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DATA_UPDATED_RESPONSE);
    }

    @Test
    void updateUserRole_userRoleUpdated_returnsConfirmationMessage() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when
        String result = userService.updateUserRole(UserTestData.getUserUpdateRoleRequestDTO());

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_ROLE_UPDATED_RESPONSE);
    }

    @Test
    void deleteAccount_accountIsDeleted_returnsConfirmationMessage() {
        // given

        // when
        String result = userService.deleteAccount(UserTestData.USER_ID);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DELETED_RESPONSE);
    }

    @Test
    void changeUserPassword_passwordIsChanged_returnsConfirmationMessage() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when
        String result = userService.changeUserPassword(UserTestData.getUserChangePasswordRequestDTO());

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_PASSWORD_UPDATED_RESPONSE);
    }
}
