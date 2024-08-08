package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.entities.User;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.UserTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserConverterTest {

    private final UserConverter userConverter = new UserConverter();

    @Test
    void toUserWithRoleDTO_conversionIsDone_returnsUserWithRoleDTO() {
        // given
        User user = getUser();

        // when
        UserWithRoleFieldResponseDTO result = userConverter.toUserWithRoleDTO(user);

        // then
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.fullName()).isEqualTo(USER_FULL_NAME);
        assertThat(result.email()).isEqualTo(USER_EMAIL);
        assertThat(result.role()).isEqualTo(USER_ROLE);
    }

    @Test
    void toUserResponseDTO_conversionIsDone_returnsUserResponseDTO() {
        // given
        User user = getUser();

        // when
        UserResponseDTO result = userConverter.toUserResponseDTO(user);

        // then
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.fullName()).isEqualTo(USER_FULL_NAME);
        assertThat(result.email()).isEqualTo(USER_EMAIL);
        assertThat(result.profilePicture()).isNull();
    }

    @Test
    void toUserEntity_conversionIsDone_returnsUserEntity() {
        // given
        UserRegistrationRequestDTO userRegistrationRequestDTO = getUserRegistrationDTO();

        // when
        User result = userConverter.toUserEntity(userRegistrationRequestDTO);

        // then
        assertThat(result.getFullName()).isEqualTo(USER_FULL_NAME);
        assertThat(result.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(result.getPassword()).isEqualTo(USER_PASSWORD);
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("id", "profilePicture", "role", "office");
    }
}
