package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.UserRole;
import com.kinandcarta.book_library.exceptions.EmailAlreadyInUseException;
import com.kinandcarta.book_library.exceptions.IncorrectPasswordException;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserResponseMessages;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    @Test
    void registerUser_theRegistrationIsSuccessful_returnsUserWithRoleDTO() throws IOException {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = UserTestData.getUserRegistrationDTO();
        User user = UserTestData.getUser();
        UserWithRoleDTO userWithRoleDTO = UserTestData.getUserWithRoleResponseDTOs().getFirst();

        given(userConverter.toUserEntity(registrationRequestDTO)).willReturn(user);

        given(officeRepository.getReferenceById(anyString())).willReturn(SharedServiceTestData.SKOPJE_OFFICE);

        Resource mockResource = mock(Resource.class);
        given(mockResource.getContentAsByteArray()).willReturn(UserTestData.USER_IMAGE_BYTES);
        given(resourceLoader.getResource(any())).willReturn(mockResource);

        given(userConverter.toUserWithRoleDTO(user)).willReturn(userWithRoleDTO);

        // when
        UserWithRoleDTO result = userManagementService.registerUser(registrationRequestDTO);

        // then
        assertThat(result).isEqualTo(userWithRoleDTO);
    }

    @Test
    void registerUser_emailAlreadyExists_throwsEmailAlreadyInUseException() {
        // given
        UserRegistrationRequestDTO userRegistrationDTO = UserTestData.getUserRegistrationDTO();

        given(userRepository.findByEmail(anyString())).willReturn(
                Optional.of(UserTestData.getUser()));

        // when && then
        assertThatExceptionOfType(EmailAlreadyInUseException.class)
                .isThrownBy(() -> userManagementService.registerUser(userRegistrationDTO))
                .withMessage("The email: " + UserTestData.USER_EMAIL + " is already in use.");
    }

    @Test
    void updateUserData_updateUserProfilePicture_returnsConfirmationMessage() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when
        String result = userManagementService.updateUserData(UserTestData.getUserUpdateDataRequestDTO());

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DATA_UPDATED_RESPONSE);
    }

    @Test
    void updateUserRole_userRoleUpdated_returnsConfirmationMessage() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when
        String result = userManagementService.updateUserRole(UserTestData.getUserUpdateRoleRequestDTO());

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_ROLE_UPDATED_RESPONSE);
    }

    @Test
    void updateUserRole_userRoleAlreadyAssignedToUser_returnsMessage() {
        // given
        User user = UserTestData.getUser();
        user.setRole(UserRole.ADMIN);
        given(userRepository.getReferenceById(any())).willReturn(user);

        // when
        String result = userManagementService.updateUserRole(UserTestData.getUserUpdateRoleRequestDTO());

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_ROLE_ALREADY_ASSIGNED_RESPONSE);
    }

    @Test
    void deleteAccount_accountIsDeleted_returnsConfirmationMessage() {
        // given

        // when
        String result = userManagementService.deleteAccount(UserTestData.USER_ID);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DELETED_RESPONSE);
    }

    @Test
    void changeUserPassword_passwordIsChanged_returnsConfirmationMessage() {
        // given
        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when
        String result = userManagementService.changeUserPassword(UserTestData.getUserChangePasswordRequestDTO());

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_PASSWORD_UPDATED_RESPONSE);
    }

    @Test
    void changeUserPassword_oldPasswordDoesNotMatch_throwsIncorrectPasswordException() {
        // given
        UserChangePasswordRequestDTO userChangePasswordRequestDTO =
                UserTestData.getUserChangePasswordRequestDTOInvalid();

        given(userRepository.getReferenceById(any())).willReturn(UserTestData.getUser());

        // when && then
        assertThatExceptionOfType(IncorrectPasswordException.class)
                .isThrownBy(() -> userManagementService.changeUserPassword(userChangePasswordRequestDTO))
                .withMessage("The password that you have entered is incorrect.");
    }
}