package com.kinandcarta.book_library.service.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.*;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.EmailAlreadyInUseException;
import com.kinandcarta.book_library.exceptions.IncorrectPasswordException;
import com.kinandcarta.book_library.exceptions.InvalidUserCredentialsException;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.impl.UserServiceImpl;
import com.kinandcarta.book_library.utils.UserServiceUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    private UserServiceUtils userServiceUtils;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_emailAlreadyExists_throwsEmailAlreadyInUseException() {
        List<User> users = getUsers();

        String email = "martin@gmail.com";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(
                "Martin Velickovski",
                email,
                "password"
        );

        given(userRepository.findByEmail(email)).willReturn(
                Optional.ofNullable(users.getFirst()));

        assertThatExceptionOfType(EmailAlreadyInUseException.class)
                .isThrownBy(() -> userService.registerUser(registrationRequestDTO))
                .withMessage("The email: " + email + " is already in use.");
    }

    @Test
    void loginUser_thereIsNoUserWithTheCredentials_throwsInvalidUserCredentialsException() {
        String email = "aleks@gmail.com";
        String password = "password";

        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(
                email,
                password
        );

        assertThatExceptionOfType(InvalidUserCredentialsException.class)
                .isThrownBy(() -> userService.loginUser(userLoginRequestDTO))
                .withMessage("The credentials that you have entered don't match.");
    }


    @Test
    void changeUserPassword_oldPasswordDoesNotMatch_throwsIncorrectPasswordException() {
        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");
        User user = getUsers().get(1);
        String oldPassword = "pw";
        String newPassword = "password";

        UserChangePasswordRequestDTO userDTO = new UserChangePasswordRequestDTO(
                userId,
                oldPassword,
                newPassword
        );

        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(user));

        assertThatExceptionOfType(IncorrectPasswordException.class)
                .isThrownBy(() -> userService.changeUserPassword(userDTO))
                .withMessage("The password that you have entered is incorrect.");
    }

    @Test
    void getAllUsers_theListIsEmpty_returnsEmptyList() {
        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsers();

        assertThat(actualResult).isEqualTo(new ArrayList<>());
    }

    @Test
    void getAllUsersWithFullName_NoMatchesWithSearchTerm_returnsEmptyList() {
        List<User> users = new ArrayList<>();
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS = new ArrayList<>();

        String fullNameSearchTerm = "Viktor Boz";

        given(userRepository.findByFullNameContainingIgnoreCaseOrderByRoleAsc(fullNameSearchTerm)).willReturn(users);

        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsersWithFullName(fullNameSearchTerm);

        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getAllUsers_theListHasAtLeastOne_returnsListOfUserWithRoleFieldResponseDTO() {
        List<User> users = getUsers();
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS = getUserWithRoleDTOs();

        given(userRepository.findAllByOrderByRoleAsc()).willReturn(users);
        given(userConverter.toUserWithRoleDTO(users.get(0))).willReturn(userWithRoleFieldResponseDTOS.get(0));
        given(userConverter.toUserWithRoleDTO(users.get(1))).willReturn(userWithRoleFieldResponseDTOS.get(1));
        given(userConverter.toUserWithRoleDTO(users.get(2))).willReturn(userWithRoleFieldResponseDTOS.get(2));

        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsers();

        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getAllUsersWithFullName_HasMatchesWithSearchTermForName_returnsListOfUserWithRoleFieldResponseDTO() {
        List<User> users = Collections.singletonList(getUsers().getFirst());
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS =
                Collections.singletonList(getUserWithRoleDTOs().getFirst());

        String fullNameSearchTerm = "Martin";

        given(userRepository.findByFullNameContainingIgnoreCaseOrderByRoleAsc(fullNameSearchTerm)).willReturn(users);
        given(userConverter.toUserWithRoleDTO(users.getFirst())).willReturn(userWithRoleFieldResponseDTOS.getFirst());

        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsersWithFullName(fullNameSearchTerm);

        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getAllUsersWithFullName_HasMatchesWithSearchTermForSurname_returnsListOfUserWithRoleFieldResponseDTO() {
        List<User> users = List.of(getUsers().get(0), getUsers().get(1));
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS =
                List.of(getUserWithRoleDTOs().get(0), getUserWithRoleDTOs().get(1));

        String fullNameSearchTerm = " Bojkovski";

        given(userRepository.findByFullNameContainingIgnoreCaseOrderByRoleAsc(fullNameSearchTerm)).willReturn(users);
        given(userConverter.toUserWithRoleDTO(users.get(0))).willReturn(userWithRoleFieldResponseDTOS.get(0));
        given(userConverter.toUserWithRoleDTO(users.get(1))).willReturn(userWithRoleFieldResponseDTOS.get(1));

        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsersWithFullName(fullNameSearchTerm);

        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getAllUsersWithFullName_HasMatchesWithSearchTermForNameAndSurname_returnsListOfUserWithRoleFieldResponseDTO() {
        List<User> users = Collections.singletonList(getUsers().get(1));
        List<UserWithRoleFieldResponseDTO> userWithRoleFieldResponseDTOS =
                Collections.singletonList(getUserWithRoleDTOs().get(1));

        String fullNameSearchTerm = "Viktorija Zlatan";

        given(userRepository.findByFullNameContainingIgnoreCaseOrderByRoleAsc(fullNameSearchTerm)).willReturn(users);
        given(userConverter.toUserWithRoleDTO(users.getFirst())).willReturn(userWithRoleFieldResponseDTOS.getFirst());

        List<UserWithRoleFieldResponseDTO> actualResult = userService.getAllUsersWithFullName(fullNameSearchTerm);

        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTOS);
    }

    @Test
    void getUserProfile_userExist_returnsUserWithoutRoleDTO() {
        List<User> users = getUsers();
        UserWithoutRoleFieldResponseDTO userWithoutRoleDTOs = getUserWithoutRoleDTOs().get(1);

        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");

        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(users.get(1)));
        given(userConverter.toUserWithoutRoleDTO(users.get(1))).willReturn(userWithoutRoleDTOs);

        UserWithoutRoleFieldResponseDTO actualResult = userService.getUserProfile(userId);

        assertThat(actualResult).isEqualTo(userWithoutRoleDTOs);
    }

    @Test
    void registerUser_theRegistrationIsSuccessful_returnsConfirmationMessage() throws IOException {
        String email = "aleks@gmail.com";
        UserRegistrationRequestDTO registrationRequestDTO = new UserRegistrationRequestDTO(
                "Aleks Velickovski",
                email,
                "password"
        );

        given(userConverter.toUserEntity(registrationRequestDTO)).willReturn(new User());

        Resource mockResource = mock(Resource.class);
        given(mockResource.getContentAsByteArray()).willReturn("classpath:image/profile-picture.png".getBytes());
        given(resourceLoader.getResource("classpath:image/profile-picture.png")).willReturn(mockResource);

        String actualResult = userService.registerUser(registrationRequestDTO);
        String expectedResult = UserServiceUtils.USER_REGISTERED_RESPONSE;

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void loginUser_loginIsValid_returnsConfirmationMessage() {
        User user = getUsers().getFirst();
        String email = "martin@gmail.com";
        String password = "pw";

        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO(
                email,
                password
        );

        given(userRepository.findByEmailAndPassword(email, password)).willReturn(Optional.ofNullable(user));

        String actualResult = userService.loginUser(userLoginRequestDTO);

        assert user != null;
        assertThat(actualResult).isEqualTo(user.getFullName());
    }

    @Test
    void updateUserData_updateUserProfilePicture_returnsConfirmationMessage() {
        User user = getUsers().getFirst();
        UUID userId = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
        String expectedResult = UserServiceUtils.USER_DATA_UPDATED_RESPONSE;
        byte[] byteArray = {(byte) 0x32, (byte) 0x13, (byte) 0x21, (byte) 0xda, (byte) 0xd2, (byte) 0x32};

        UserUpdateDataRequestDTO userDTO = new UserUpdateDataRequestDTO(
                userId,
                "",
                byteArray
        );

        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(user));

        String actualResult = userService.updateUserData(userDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void updateUserRole_userRoleUpdated_returnsConfirmationMessage() {
        User user = getUsers().get(1);
        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");
        String expectedMessage = UserServiceUtils.USER_ROLE_UPDATED_RESPONSE;

        UserUpdateRoleRequestDTO userDTO = new UserUpdateRoleRequestDTO(
                userId,
                "USER"
        );


        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        String actualResult = userService.updateUserRole(userDTO);

        assertThat(actualResult).isEqualTo(expectedMessage);
    }

    @Test
    void deleteAccount_accountIsDeleted_returnsConfirmationMessage() {
        UUID userId = UUID.fromString("80707649-1be3-43db-ae7e-f374fe09fcb2");
        String expectedResult = UserServiceUtils.USER_DELETED_RESPONSE;

        String actualResult = userService.deleteAccount(userId);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void changeUserPassword_passwordIsChanged_returnsConfirmationMessage() {
        UUID userId = UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43");
        User user = getUsers().get(1);
        String oldPassword = "Pw";
        String newPassword = "password";
        String expectedResult = UserServiceUtils.USER_PASSWORD_UPDATED_RESPONSE;

        UserChangePasswordRequestDTO userDTO = new UserChangePasswordRequestDTO(
                userId,
                oldPassword,
                newPassword
        );

        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(user));

        String actualResult = userService.changeUserPassword(userDTO);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    public List<User> getUsers() {
        User user1 = new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw");

        User user2 = new User(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"), "David Bojkovski", null,
                "david@gmail.com", "ADMIN", "Pw");

        User user3 = new User(UUID.fromString("80707649-1be3-43db-ae7e-f374fe09fcb2"), "Viktorija Zlatanovska", null,
                "viktorija@gmail.com", "Admin", "password");

        return List.of(user1, user2, user3);
    }

    public List<UserWithRoleFieldResponseDTO> getUserWithRoleDTOs() {
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

    public List<UserWithoutRoleFieldResponseDTO> getUserWithoutRoleDTOs() {
        UserWithoutRoleFieldResponseDTO user1 =
                new UserWithoutRoleFieldResponseDTO(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                        "Martin Bojkovski", null, "martin@gmail.com");

        UserWithoutRoleFieldResponseDTO user2 =
                new UserWithoutRoleFieldResponseDTO(UUID.fromString("4cfe701c-45ee-4a22-a8e1-bde61acd6f43"),
                        "David Bojkovski", null, "david@gmail.com");

        UserWithoutRoleFieldResponseDTO user3 =
                new UserWithoutRoleFieldResponseDTO(UUID.fromString("80707649-1be3-43db-ae7e-f374fe09fcb2"),
                        "Viktorija Zlatanovska", null, "viktorija@gmail.com");

        return List.of(user1, user2, user3);
    }


}
