package com.kinandcarta.book_library.service.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.*;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.EmailAlreadyInUseException;
import com.kinandcarta.book_library.exceptions.IncorrectPasswordException;
import com.kinandcarta.book_library.exceptions.InvalidUserCredentialsException;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.impl.UserServiceImpl;
import com.kinandcarta.book_library.utils.UserResponseMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @InjectMocks
    private UserServiceImpl userService;

    private static final String IMAGE_PATH = "classpath:image/profile-picture.png";

    @Test
    void registerUser_emailAlreadyExists_throwsEmailAlreadyInUseException() {
        // given
        List<User> users = getUsers();

        String email = "martin@gmail.com";
        String fullName = "Martin Velickovski";
        String password = "password";

        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(
                fullName,
                email,
                password
        );

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
        String email = "aleks@gmail.com";
        String password = "password";

        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(
                email,
                password
        );

        // when && then
        assertThatExceptionOfType(InvalidUserCredentialsException.class)
                .isThrownBy(() -> userService.loginUser(userLoginRequestDTO))
                .withMessage("The credentials that you have entered don't match.");
    }


    @Test
    void changeUserPassword_oldPasswordDoesNotMatch_throwsIncorrectPasswordException() {
        // given
        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");
        User user = getUsers().get(1);
        String oldPassword = "pw";
        String newPassword = "password";

        UserChangePasswordRequestDTO userDTO = new UserChangePasswordRequestDTO(
                userId,
                oldPassword,
                newPassword
        );

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

        // when && then
        assertThatExceptionOfType(IncorrectPasswordException.class)
                .isThrownBy(() -> userService.changeUserPassword(userDTO))
                .withMessage("The password that you have entered is incorrect.");
    }

    @Test
    void getAllUsers_theListHasAtLeastOne_returnsListOfUserWithRoleFieldResponseDTO() {
        // given
        List<User> users = getUsers();
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS = getUserWithRoleResponseDTOs();

        given(userRepository.findAllByOrderByRoleAsc()).willReturn(users);
        given(userConverter.toUserWithRoleDTO(any())).willReturn(userWithRoleFieldResponseDTOS.get(0),
                userWithRoleFieldResponseDTOS.get(1), userWithRoleFieldResponseDTOS.get(2));

        // when
        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsers();

        // then
        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getAllUsersWithFullName_HasMatchesWithSearchTerm_returnsListOfUserWithRoleFieldResponseDTO() {
        // given
        List<User> users = List.of(getUsers().getFirst());
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS =
                List.of(getUserWithRoleResponseDTOs().getFirst());

        String fullNameSearchTerm = "Martin";

        given(userRepository.findByFullNameContainingIgnoreCaseOrderByRoleAsc(anyString())).willReturn(users);
        given(userConverter.toUserWithRoleDTO(users.getFirst())).willReturn(userWithRoleFieldResponseDTOS.getFirst());

        // when
        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsersWithFullName(fullNameSearchTerm);

        // then
        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getUserProfile_userExist_returnsUserWithoutRoleDTO() {
        // given
        User user = getUsers().get(1);
        UserResponseDTO userWithoutRoleDTOs = getUserResponseDTOs().get(1);

        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(userConverter.toUserResponseDTO(any())).willReturn(userWithoutRoleDTOs);

        // when
        UserResponseDTO actualResult = userService.getUserProfile(userId);

        // then
        assertThat(actualResult).isEqualTo(userWithoutRoleDTOs);
    }

    @Test
    void registerUser_theRegistrationIsSuccessful_returnsConfirmationMessage() throws IOException {
        // given
        String email = "aleks@gmail.com";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(
                "Aleks Velickovski",
                email,
                "password"
        );

        given(userConverter.toUserEntity(registrationRequestDTO)).willReturn(new User());

        Resource mockResource = mock(Resource.class);
        given(mockResource.getContentAsByteArray()).willReturn(IMAGE_PATH.getBytes());
        given(resourceLoader.getResource(any())).willReturn(mockResource);

        // when
        String actualResult = userService.registerUser(registrationRequestDTO);

        // then
        assertThat(actualResult).isEqualTo(UserResponseMessages.USER_REGISTERED_RESPONSE);
    }

    @Test
    void loginUser_loginIsValid_returnsConfirmationMessage() {
        // given
        User user = getUsers().getFirst();
        String email = "martin@gmail.com";
        String password = "pw";

        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(
                email,
                password
        );

        given(userRepository.findByEmailAndPassword(anyString(), anyString())).willReturn(Optional.of(user));

        // when
        String actualResult = userService.loginUser(userLoginRequestDTO);

        // then
        assertThat(actualResult).isEqualTo(user.getFullName());
    }

    @Test
    void updateUserData_updateUserProfilePicture_returnsConfirmationMessage() {
        // given
        User user = getUsers().getFirst();
        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
        byte[] byteArray = {(byte) 0x32, (byte) 0x13, (byte) 0x21, (byte) 0xda, (byte) 0xd2, (byte) 0x32};

        UserUpdateDataRequestDTO userDTO = new UserUpdateDataRequestDTO(
                userId,
                "",
                byteArray
        );

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

        // when
        String actualResult = userService.updateUserData(userDTO);

        // then
        assertThat(actualResult).isEqualTo(UserResponseMessages.USER_DATA_UPDATED_RESPONSE);
    }

    @Test
    void updateUserRole_userRoleUpdated_returnsConfirmationMessage() {
        // given
        User user = getUsers().get(1);
        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");

        UserUpdateRoleRequestDTO userDTO = new UserUpdateRoleRequestDTO(
                userId,
                "USER"
        );

        given(userRepository.findById(any())).willReturn(Optional.of(user));

        // when
        String actualResult = userService.updateUserRole(userDTO);

        // then
        assertThat(actualResult).isEqualTo(UserResponseMessages.USER_ROLE_UPDATED_RESPONSE);
    }

    @Test
    void deleteAccount_accountIsDeleted_returnsConfirmationMessage() {
        // given
        UUID userId = UUID.fromString("80707649-1be3-43db-ae7e-f374fe09fcb2");

        // when
        String actualResult = userService.deleteAccount(userId);

        // then
        assertThat(actualResult).isEqualTo(UserResponseMessages.USER_DELETED_RESPONSE);
    }

    @Test
    void changeUserPassword_passwordIsChanged_returnsConfirmationMessage() {
        // given
        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");
        User user = getUsers().get(1);
        String oldPassword = "Pw";
        String newPassword = "password";

        UserChangePasswordRequestDTO userDTO = new UserChangePasswordRequestDTO(
                userId,
                oldPassword,
                newPassword
        );

        given(userRepository.findById(any())).willReturn(Optional.of(user));

        // when
        String actualResult = userService.changeUserPassword(userDTO);

        // then
        assertThat(actualResult).isEqualTo(UserResponseMessages.USER_PASSWORD_UPDATED_RESPONSE);
    }

    private List<User> getUsers() {
        User user1 = new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw");

        User user2 = new User(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"), "David Bojkovski", null,
                "david@gmail.com", "ADMIN", "Pw");

        User user3 = new User(UUID.fromString("80707649-1be3-43db-ae7e-f374fe09fcb2"), "Viktorija Zlatanovska", null,
                "viktorija@gmail.com", "Admin", "password");

        return List.of(user1, user2, user3);
    }

    private List<UserWithRoleFieldResponseDTO> getUserWithRoleResponseDTOs() {
        UserWithRoleFieldResponseDTO user1 =
                new UserWithRoleFieldResponseDTO(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                        "Martin Bojkovski", "martin@gmail.com", "USER");

        UserWithRoleFieldResponseDTO user2 =
                new UserWithRoleFieldResponseDTO(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"),
                        "David Bojkovski", "david@gmail.com", "ADMIN");

        UserWithRoleFieldResponseDTO user3 =
                new UserWithRoleFieldResponseDTO(UUID.fromString("80707649-1be3-43db-ae7e-f374fe09fcb2"),
                        "Viktorija Zlatanovska", "viktorija@gmail.com", "ADMIN");

        return List.of(user1, user2, user3);
    }

    private List<UserResponseDTO> getUserResponseDTOs() {
        UserResponseDTO user1 =
                new UserResponseDTO(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                        "Martin Bojkovski", null, "martin@gmail.com");

        UserResponseDTO user2 =
                new UserResponseDTO(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"),
                        "David Bojkovski", null, "david@gmail.com");

        UserResponseDTO user3 =
                new UserResponseDTO(UUID.fromString("80707649-1be3-43db-ae7e-f374fe09fcb2"),
                        "Viktorija Zlatanovska", null, "viktorija@gmail.com");

        return List.of(user1, user2, user3);
    }


}
