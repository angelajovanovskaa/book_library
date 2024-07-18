package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @InjectMocks
    UserConverter userConverter;

    @Test
    void toUserWithRoleDTO_conversionIsDone_returnsUserWithRoleDTO(){
        // given
        User user = getUser();
        UserWithRoleFieldResponseDTO userWithRoleFieldResponseDTO = getUserWithRoleResponseDTO();

        // when
        UserWithRoleFieldResponseDTO actualResult = userConverter.toUserWithRoleDTO(user);

        // then
        assertThat(actualResult).isEqualTo(userWithRoleFieldResponseDTO);
    }

    @Test
    void toUserResponseDTO_conversionIsDone_returnsUserResponseDTO(){
        // given
        User user = getUser();
        UserResponseDTO userResponseDTO = getUserResponseDTO();

        // when
        UserResponseDTO actualResult = userConverter.toUserResponseDTO(user);

        // then
        assertThat(actualResult).isEqualTo(userResponseDTO);
    }

    @Test
    void toUserEntity_conversionIsDone_returnsUserEntity(){
        // given
        User user = getUser();
        UserRegistrationRequestDTO userRegistrationRequestDTO = getUserRegistrationRequestDTO();

        // when
        User actualResult = userConverter.toUserEntity(userRegistrationRequestDTO);

        // then
        assertThat(actualResult.getFullName()).isEqualTo(user.getFullName());
        assertThat(actualResult.getEmail()).isEqualTo(user.getEmail());
        assertThat(actualResult.getPassword()).isEqualTo(user.getPassword());

    }

    private User getUser() {
        return new User(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"), "Martin Bojkovski", null,
                "martin@gmail.com", "USER", "pw");
    }

    private UserWithRoleFieldResponseDTO getUserWithRoleResponseDTO() {
        return new UserWithRoleFieldResponseDTO(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                        "Martin Bojkovski", "martin@gmail.com", "USER");
    }

    private UserResponseDTO getUserResponseDTO() {
        return new UserResponseDTO(UUID.fromString("d393861b-c1e1-4d21-bffe-8cf4c4f3c142"),
                        "Martin Bojkovski", null, "martin@gmail.com");
    }

    private UserRegistrationRequestDTO getUserRegistrationRequestDTO(){
        return new UserRegistrationRequestDTO("Martin Bojkovski", "martin@gmail.com","pw");
    }
}
