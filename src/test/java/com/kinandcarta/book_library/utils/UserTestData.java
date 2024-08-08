package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.*;
import com.kinandcarta.book_library.entities.User;
import java.util.*;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.OfficeTestData.OFFICE;

@UtilityClass
public class UserTestData {
    public static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141");
    public static final String USER_FULL_NAME = "fullname1";
    public static final String USER_EMAIL = "user1@gmail.com";
    public static final String USER_PASSWORD = "password1";
    public static final String USER_ROLE = "USER";
    public static final String USER_IMAGE_PATH = "classpath:image/profile-picture.png";

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        User user1 = new User(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141"),
                "fullname1",
                null,
                "user1@gmail.com",
                "USER",
                "password1",
                OFFICE
        );
        users.add(user1);
        User user2 = new User(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "fullname2",
                null,
                "user2@gmail.com",
                "USER",
                "password2",
                OFFICE
        );
        users.add(user2);

        return users;
    }

    public static Set<User> getSetUsers() {
        Set<User> users = new HashSet<>();
        users.add(getUser());

        return users;
    }

    public static User getUser() {
        return getUsers().getFirst();
    }

    public static List<UserWithRoleFieldResponseDTO> getUserWithRoleResponseDTOs() {
        List<UserWithRoleFieldResponseDTO> users = new ArrayList<>();
        UserWithRoleFieldResponseDTO user1 = new UserWithRoleFieldResponseDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141"),
                "user1",
                "user1@gmail.com",
                "USER"
        );
        users.add(user1);
        UserWithRoleFieldResponseDTO user2 = new UserWithRoleFieldResponseDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "user2",
                "user2@gmail.com",
                "USER"
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
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141"),
                "user1",
                "user1@gmail.com",
                null
        );
        users.add(user1);
        UserResponseDTO user2 = new UserResponseDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "user1",
                "user1@gmail.com",
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
                "fullname1",
                "user1@gmail.com",
                OFFICE.getName(),
                "password1"
        );
    }

    public static UserLoginRequestDTO getUserLoginRequestDTO() {
        return new UserLoginRequestDTO(
                "user1@gmail.com",
                "password1"
        );
    }

    public static UserChangePasswordRequestDTO getUserChangePasswordRequestDTOValid() {
        return new UserChangePasswordRequestDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141"),
                "password1",
                "newPassword"
        );
    }

    public static UserChangePasswordRequestDTO getUserChangePasswordRequestDTOInvalid() {
        return new UserChangePasswordRequestDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141"),
                "incorrectPassword",
                "newPassword"
        );
    }

    public static UserUpdateDataRequestDTO getUserUpdateDataRequestDTO() {
        return new UserUpdateDataRequestDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141"),
                "newFullname",
                "newProfilePicture".getBytes()
        );
    }

    public static UserUpdateRoleRequestDTO getUserUpdateRoleRequestDTO() {
        return new UserUpdateRoleRequestDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141"),
                "ADMIN"
        );
    }
}
