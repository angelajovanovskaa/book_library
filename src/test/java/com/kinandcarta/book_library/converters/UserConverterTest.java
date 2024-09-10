package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.UserRole;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserConverterTest {

    private final UserConverter userConverter = new UserConverter();

    @Test
    void toUserWithRoleDTO_conversionIsDone_returnsUserWithRoleDTO() {
        // given

        // when
        UserWithRoleDTO actualResult = userConverter.toUserWithRoleDTO(UserTestData.getUser());

        // then
        assertThat(actualResult.userId()).isEqualTo(UserTestData.USER_ID);
        assertThat(actualResult.fullName()).isEqualTo(UserTestData.USER_FULL_NAME);
        assertThat(actualResult.email()).isEqualTo(UserTestData.USER_EMAIL);
        assertThat(actualResult.role()).isEqualTo(UserRole.USER);
    }

    @Test
    void toUserProfileDTO_conversionIsDone_returnsUserProfileDTO() {
        // given

        // when
        UserProfileDTO actualResult = userConverter.toUserProfileDTO(UserTestData.getUser());

        // then
        assertThat(actualResult.userId()).isEqualTo(UserTestData.USER_ID);
        assertThat(actualResult.fullName()).isEqualTo(UserTestData.USER_FULL_NAME);
        assertThat(actualResult.email()).isEqualTo(UserTestData.USER_EMAIL);
        assertThat(actualResult.profilePicture()).isEqualTo(UserTestData.USER_IMAGE_BYTES);
    }

    @Test
    void toUserEntity_conversionIsDone_returnsUserEntity() {
        // given
        String password = UserTestData.USER_ENCODED_PASSWORD;
        byte[] profilePicture = UserTestData.USER_IMAGE_BYTES;
        Office office = SharedServiceTestData.SKOPJE_OFFICE;

        // when
        User actualResult =
                userConverter.toUserEntity(UserTestData.getUserRegistrationDTO(), password, office, profilePicture);

        // then
        assertThat(actualResult.getFullName()).isEqualTo(UserTestData.USER_FULL_NAME);
        assertThat(actualResult.getEmail()).isEqualTo(UserTestData.USER_EMAIL);
        assertThat(actualResult.getPassword()).isEqualTo(password);
        assertThat(actualResult.getOffice()).isEqualTo(office);
        assertThat(actualResult.getProfilePicture()).isEqualTo(profilePicture);
        assertThat(actualResult.getRole()).isEqualTo(UserRole.USER);
        assertThat(actualResult).hasNoNullFieldsOrPropertiesExcept("id");
    }
}
