package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.*;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.EmailAlreadyInUseException;
import com.kinandcarta.book_library.exceptions.IncorrectPasswordException;
import com.kinandcarta.book_library.exceptions.InvalidUserCredentialsException;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.utils.UserResponseMessages;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static com.kinandcarta.book_library.utils.OfficeTestData.OFFICE;
import static com.kinandcarta.book_library.utils.UserTestData.*;
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
        List<User> users = getUsers();
        User user = getUser();
        String email = user.getEmail();
        UserRegistrationRequestDTO registrationRequestDTO = getUserRegistrationDTO();

        given(userRepository.findByEmail(anyString())).willReturn(
                Optional.of(users.getFirst()));

        // when && then
        assertThatExceptionOfType(EmailAlreadyInUseException.class)
                .isThrownBy(() -> userService.registerUser(registrationRequestDTO))
                .withMessage("The email: " + email + " is already in use.");
    }

    @Test
    void loginUser_thereIsNoUserWithTheCredentials_throwsInvalidUserCredentialsException() {
        // given
        UserLoginRequestDTO userLoginRequestDTO = getUserLoginRequestDTO();

        // when && then
        assertThatExceptionOfType(InvalidUserCredentialsException.class)
                .isThrownBy(() -> userService.loginUser(userLoginRequestDTO))
                .withMessage("The credentials that you have entered don't match.");
    }

    @Test
    void changeUserPassword_oldPasswordDoesNotMatch_throwsIncorrectPasswordException() {
        // given
        User user = getUser();
        UserChangePasswordRequestDTO userChangePasswordRequestDTO = getUserChangePasswordRequestDTOInvalid();

        given(userRepository.getReferenceById(any())).willReturn(user);

        // when && then
        assertThatExceptionOfType(IncorrectPasswordException.class)
                .isThrownBy(() -> userService.changeUserPassword(userChangePasswordRequestDTO))
                .withMessage("The password that you have entered is incorrect.");
    }

    @Test
    void getAllUsers_theListHasAtLeastOne_returnsListOfUserWithRoleFieldResponseDTO() {
        // given
        List<User> users = getUsers();
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS = getUserWithRoleResponseDTOs();

        given(userRepository.findAllByOffice_NameOrderByRoleAsc(anyString())).willReturn(users);
        given(userConverter.toUserWithRoleDTO(any())).willReturn(userWithRoleFieldResponseDTOS.get(0),
                userWithRoleFieldResponseDTOS.get(1));

        // when
        List<UserWithRoleFieldResponseDTO> result = userService.getAllUsers(OFFICE.getName());

        // then
        assertThat(result).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getAllUsersWithFullName_HasMatchesWithSearchTerm_returnsListOfUserWithRoleFieldResponseDTO() {
        // given
        List<User> users = List.of(getUser());
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS = List.of(getUserWithRoleResponseDTO());

        given(userRepository.findByOffice_NameAndFullNameContainingIgnoreCaseOrderByRoleAsc(anyString(),
                anyString())).willReturn(users);
        given(userConverter.toUserWithRoleDTO(users.getFirst())).willReturn(userWithRoleFieldResponseDTOS.getFirst());

        // when
        List<UserWithRoleFieldResponseDTO> result =
                userService.getAllUsersWithFullName(OFFICE.getName(), USER_FULL_NAME);

        // then
        assertThat(result).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getUserProfile_userExist_returnsUserWithoutRoleDTO() {
        // given
        User user = getUser();
        UserResponseDTO userWithoutRoleDTOs = getUserResponseDTO();
        UUID userId = user.getId();

        given(userRepository.getReferenceById(any())).willReturn(user);
        given(userConverter.toUserResponseDTO(any())).willReturn(userWithoutRoleDTOs);

        // when
        UserResponseDTO result = userService.getUserProfile(userId);

        // then
        assertThat(result).isEqualTo(userWithoutRoleDTOs);
    }

    @Test
    void registerUser_theRegistrationIsSuccessful_returnsConfirmationMessage() throws IOException {
        // given
        UserRegistrationRequestDTO registrationRequestDTO = getUserRegistrationDTO();

        given(userConverter.toUserEntity(registrationRequestDTO)).willReturn(new User());

        given(officeRepository.getReferenceById(anyString())).willReturn(OFFICE);

        Resource mockResource = mock(Resource.class);
        given(mockResource.getContentAsByteArray()).willReturn(USER_IMAGE_PATH.getBytes());
        given(resourceLoader.getResource(any())).willReturn(mockResource);

        // when
        String result = userService.registerUser(registrationRequestDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_REGISTERED_RESPONSE);
    }

    @Test
    void loginUser_loginIsValid_returnsConfirmationMessage() {
        // given
        User user = getUsers().getFirst();
        UserLoginRequestDTO userLoginRequestDTO = getUserLoginRequestDTO();

        given(userRepository.findByEmailAndPassword(anyString(), anyString())).willReturn(Optional.of(user));

        // when
        String result = userService.loginUser(userLoginRequestDTO);

        // then
        assertThat(result).isEqualTo(user.getFullName());
    }

    @Test
    void updateUserData_updateUserProfilePicture_returnsConfirmationMessage() {
        // given
        User user = getUser();
        UserUpdateDataRequestDTO userUpdateDataRequestDTO = getUserUpdateDataRequestDTO();

        given(userRepository.getReferenceById(any())).willReturn(user);

        // when
        String result = userService.updateUserData(userUpdateDataRequestDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DATA_UPDATED_RESPONSE);
    }

    @Test
    void updateUserRole_userRoleUpdated_returnsConfirmationMessage() {
        // given
        User user = getUser();
        UserUpdateRoleRequestDTO userUpdateRoleRequestDTO = getUserUpdateRoleRequestDTO();

        given(userRepository.getReferenceById(any())).willReturn(user);

        // when
        String result = userService.updateUserRole(userUpdateRoleRequestDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_ROLE_UPDATED_RESPONSE);
    }

    @Test
    void deleteAccount_accountIsDeleted_returnsConfirmationMessage() {
        // given

        // when
        String result = userService.deleteAccount(USER_ID);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_DELETED_RESPONSE);
    }

    @Test
    void changeUserPassword_passwordIsChanged_returnsConfirmationMessage() {
        // given
        User user = getUser();
        UserChangePasswordRequestDTO userDTO = getUserChangePasswordRequestDTOValid();

        given(userRepository.getReferenceById(any())).willReturn(user);

        // when
        String result = userService.changeUserPassword(userDTO);

        // then
        assertThat(result).isEqualTo(UserResponseMessages.USER_PASSWORD_UPDATED_RESPONSE);
    }
}
