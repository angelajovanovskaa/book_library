package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.SharedTestData.SKOPJE_OFFICE;

@UtilityClass
public class UserTestData {
    public static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141");
    public static final String USER_FULL_NAME = "fullname1";
    public static final String USER_EMAIL = "user1@gmail.com";
    public static final String USER_PASSWORD = "password";
    public static final String USER_NEW_PASSWORD = "newPassword";
    public static final String USER_ROLE = "USER";
    public static final String USER_IMAGE_PATH = "classpath:image/profile-picture.png";

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        User user1 = new User(
                USER_ID,
                USER_FULL_NAME,
                null,
                USER_EMAIL,
                USER_ROLE,
                USER_PASSWORD,
                SKOPJE_OFFICE
        );
        users.add(user1);
        User user2 = new User(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "fullname2",
                null,
                "user2@gmail.com",
                USER_ROLE,
                USER_PASSWORD,
                SKOPJE_OFFICE
        );
        users.add(user2);

        return users;
    }

    public static User getUser() {
        return getUsers().getFirst();
    }

    public static List<UserWithRoleFieldResponseDTO> getUserWithRoleResponseDTOs() {
        List<UserWithRoleFieldResponseDTO> users = new ArrayList<>();
        UserWithRoleFieldResponseDTO user1 = new UserWithRoleFieldResponseDTO(
                USER_ID,
                USER_FULL_NAME,
                USER_EMAIL,
                USER_ROLE
        );
        users.add(user1);
        UserWithRoleFieldResponseDTO user2 = new UserWithRoleFieldResponseDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "user2",
                "user2@gmail.com",
                USER_ROLE
        );
        users.add(user2);

        return users;
    }

    public static UserWithRoleFieldResponseDTO getUserWithRoleResponseDTO() {
        return getUserWithRoleResponseDTOs().getFirst();
    }

    public static List<UserResponseDTO> getUserResponseDTOs() {
        List<UserResponseDTO> users = new ArrayList<>();
        UserResponseDTO user1 = new UserResponseDTO(
                USER_ID,
                USER_FULL_NAME,
                USER_EMAIL,
                null
        );
        users.add(user1);
        UserResponseDTO user2 = new UserResponseDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "user2",
                "user2@gmail.com",
                null
        );
        users.add(user2);

        return users;
    }

    public static UserResponseDTO getUserResponseDTO() {
        return getUserResponseDTOs().getFirst();
    }

    public static UserRegistrationRequestDTO getUserRegistrationDTO() {
        return new UserRegistrationRequestDTO(
                USER_FULL_NAME,
                USER_EMAIL,
                SKOPJE_OFFICE.getName(),
                USER_PASSWORD
        );
    }

    public static UserLoginRequestDTO getUserLoginRequestDTO() {
        return new UserLoginRequestDTO(
                USER_FULL_NAME,
                USER_PASSWORD
        );
    }

    public static UserChangePasswordRequestDTO getUserChangePasswordRequestDTO() {
        return new UserChangePasswordRequestDTO(
                USER_ID,
                USER_PASSWORD,
                USER_NEW_PASSWORD
        );
    }

    public static UserChangePasswordRequestDTO getUserChangePasswordRequestDTOInvalid() {
        return new UserChangePasswordRequestDTO(
                USER_ID,
                USER_PASSWORD + "incorrect",
                USER_NEW_PASSWORD
        );
    }

    public static UserUpdateDataRequestDTO getUserUpdateDataRequestDTO() {
        return new UserUpdateDataRequestDTO(
                USER_ID,
                USER_FULL_NAME + "new",
                USER_IMAGE_PATH.getBytes()
        );
    }

    public static UserUpdateRoleRequestDTO getUserUpdateRoleRequestDTO() {
        return new UserUpdateRoleRequestDTO(
                USER_ID,
                "ADMIN"
        );
    }
}
