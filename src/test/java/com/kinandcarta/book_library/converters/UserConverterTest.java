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
    private static final Office SKOPJE_OFFICE = new Office("Skopje");

    private final UserConverter userConverter = new UserConverter();

    @Test
    void toUserWithRoleDTO_conversionIsDone_returnsUserWithRoleDTO() {
        // given
        User user = getUser();
        UserWithRoleFieldResponseDTO userWithRoleFieldResponseDTO = getUserWithRoleResponseDTO();

        // when
        UserWithRoleFieldResponseDTO result = userConverter.toUserWithRoleDTO(user);

        // then
        assertThat(result).isEqualTo(userWithRoleFieldResponseDTO);
    }

    @Test
    void toUserResponseDTO_conversionIsDone_returnsUserResponseDTO() {
        // given
        User user = getUser();
        UserResponseDTO userResponseDTO = getUserResponseDTO();

        // when
        UserResponseDTO result = userConverter.toUserResponseDTO(user);

        // then
        assertThat(result).isEqualTo(userResponseDTO);
    }

    @Test
    void toUserEntity_conversionIsDone_returnsUserEntity() {
        // given
        User user = getUser();
        UserRegistrationRequestDTO userRegistrationRequestDTO = getUserRegistrationRequestDTO();

        // when
        User result = userConverter.toUserEntity(userRegistrationRequestDTO);

        // then
        assertThat(result).usingRecursiveComparison().ignoringFields("id", "office").isEqualTo(user);

    }

    private User getUser() {
        return new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw", SKOPJE_OFFICE);
    }

    private UserWithRoleFieldResponseDTO getUserWithRoleResponseDTO() {
        return new UserWithRoleFieldResponseDTO(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "Martin Bojkovski", "martin@gmail.com", "USER");
    }

    private UserResponseDTO getUserResponseDTO() {
        return new UserResponseDTO(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                "Martin Bojkovski", null, "martin@gmail.com");
    }

    private UserRegistrationRequestDTO getUserRegistrationRequestDTO() {
        return new UserRegistrationRequestDTO("Martin Bojkovski", "martin@gmail.com", "pw");
    }
}
