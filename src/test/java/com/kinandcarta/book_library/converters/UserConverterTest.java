package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserConverterTest {
    private static final UUID USER_ID = UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142");
    private static final String FULL_NAME = "Martin Bojkovski";
    private static final String EMAIL = "martin.bojkovski@kinandcarta.com";
    private static final String ROLE = "USER";
    private static final String PASSWORD = "pw";
    private static final Office SKOPJE_OFFICE = new Office("Skopje");

    private final UserConverter userConverter = new UserConverter();

    @Test
    void toUserWithRoleDTO_conversionIsDone_returnsUserWithRoleDTO() {
        // given
        User user = new User(USER_ID, FULL_NAME, null, EMAIL, ROLE, PASSWORD, SKOPJE_OFFICE);

        // when
        UserWithRoleFieldResponseDTO result = userConverter.toUserWithRoleDTO(user);

        // then
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.fullName()).isEqualTo(FULL_NAME);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.role()).isEqualTo(ROLE);
    }

    @Test
    void toUserResponseDTO_conversionIsDone_returnsUserResponseDTO() {
        // given
        User user = new User(USER_ID, FULL_NAME, null, EMAIL, ROLE, PASSWORD, SKOPJE_OFFICE);

        // when
        UserResponseDTO result = userConverter.toUserResponseDTO(user);

        // then
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.fullName()).isEqualTo(FULL_NAME);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.profilePicture()).isNull();
    }

    @Test
    void toUserEntity_conversionIsDone_returnsUserEntity() {
        // given
        UserRegistrationRequestDTO userRegistrationRequestDTO = new UserRegistrationRequestDTO(FULL_NAME, EMAIL,
                SKOPJE_OFFICE.getName(), PASSWORD);

        // when
        User result = userConverter.toUserEntity(userRegistrationRequestDTO);

        // then
        assertThat(result.getFullName()).isEqualTo(FULL_NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
    }
}
