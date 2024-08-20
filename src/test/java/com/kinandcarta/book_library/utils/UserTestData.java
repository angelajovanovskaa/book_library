package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.UserRole;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;

import static com.kinandcarta.book_library.utils.SharedServiceTestData.SKOPJE_OFFICE;
import static com.kinandcarta.book_library.utils.SharedServiceTestData.SKOPJE_OFFICE_NAME;

@UtilityClass
public class UserTestData {
    private static final String USER_NEW_PASSWORD = "newPassword";
    private static final String USER_IMAGE_PATH = "classpath:image/profile-picture.png";
    public static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c141");
    public static final String USER_FULL_NAME = "fullname1";
    public static final String USER_EMAIL = "user1@gmail.com";
    public static final String USER_PASSWORD = "password";
    public static final byte[] USER_IMAGE_BYTES = USER_IMAGE_PATH.getBytes();

    public static List<User> getUsers() {
        User user1 = new User(
                USER_ID,
                USER_FULL_NAME,
                USER_IMAGE_BYTES,
                USER_EMAIL,
                UserRole.USER,
                USER_PASSWORD,
                SKOPJE_OFFICE
        );
        User user2 = new User(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "fullname2",
                USER_IMAGE_BYTES,
                "user2@gmail.com",
                UserRole.USER,
                USER_PASSWORD,
                SKOPJE_OFFICE
        );

        return List.of(user1, user2);
    }

    public static User getUser() {
        return getUsers().getFirst();
    }

    public static List<UserWithRoleFieldResponseDTO> getUserWithRoleResponseDTOs() {
        UserWithRoleFieldResponseDTO user1 = new UserWithRoleFieldResponseDTO(
                USER_ID,
                USER_FULL_NAME,
                USER_EMAIL,
                UserRole.USER
        );
        UserWithRoleFieldResponseDTO user2 = new UserWithRoleFieldResponseDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "user2",
                "user2@gmail.com",
                UserRole.USER
        );

        return List.of(user1, user2);
    }

    public static List<UserProfileDTO> getUserResponseDTOs() {
        UserProfileDTO user1 = new UserProfileDTO(
                USER_ID,
                USER_FULL_NAME,
                USER_EMAIL,
                null
        );
        UserProfileDTO user2 = new UserProfileDTO(
                UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "user2",
                "user2@gmail.com",
                null
        );

        return List.of(user1, user2);
    }

    public static UserProfileDTO getUserProfileDTO() {
        return getUserResponseDTOs().getFirst();
    }

    public static UserRegistrationRequestDTO getUserRegistrationDTO() {
        return new UserRegistrationRequestDTO(
                USER_FULL_NAME,
                USER_EMAIL,
                SKOPJE_OFFICE_NAME,
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
                UserRole.ADMIN
        );
    }
}
