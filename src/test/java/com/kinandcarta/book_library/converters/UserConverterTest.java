package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserConverterTest {

    private final UserConverter userConverter = new UserConverter();

    @Test
    void toUserWithRoleDTO_conversionIsDone_returnsUserWithRoleDTO() {
        // given

        // when
        UserWithRoleFieldResponseDTO actualResult = userConverter.toUserWithRoleDTO(UserTestData.getUser());

        // then
        assertThat(actualResult.userId()).isEqualTo(UserTestData.USER_ID);
        assertThat(actualResult.fullName()).isEqualTo(UserTestData.USER_FULL_NAME);
        assertThat(actualResult.email()).isEqualTo(UserTestData.USER_EMAIL);
        assertThat(actualResult.role()).isEqualTo(UserTestData.USER_ROLE);
    }

    @Test
    void toUserResponseDTO_conversionIsDone_returnsUserResponseDTO() {
        // given

        // when
        UserResponseDTO actualResult = userConverter.toUserResponseDTO(UserTestData.getUser());

        // then
        assertThat(actualResult.userId()).isEqualTo(UserTestData.USER_ID);
        assertThat(actualResult.fullName()).isEqualTo(UserTestData.USER_FULL_NAME);
        assertThat(actualResult.email()).isEqualTo(UserTestData.USER_EMAIL);
        assertThat(actualResult.profilePicture()).isEqualTo(UserTestData.USER_IMAGE_BYTES);
    }

    @Test
    void toUserEntity_conversionIsDone_returnsUserEntity() {
        // given

        // when
        User actualResult = userConverter.toUserEntity(UserTestData.getUserRegistrationDTO());

        // then
        assertThat(actualResult.getFullName()).isEqualTo(UserTestData.USER_FULL_NAME);
        assertThat(actualResult.getEmail()).isEqualTo(UserTestData.USER_EMAIL);
        assertThat(actualResult.getPassword()).isEqualTo(UserTestData.USER_PASSWORD);
        assertThat(actualResult).hasNoNullFieldsOrPropertiesExcept("id", "profilePicture", "role", "office");
    }
}
