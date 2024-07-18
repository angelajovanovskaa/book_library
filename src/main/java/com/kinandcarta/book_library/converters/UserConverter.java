package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
import com.kinandcarta.book_library.dtos.UserResponseDTO;
import com.kinandcarta.book_library.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for providing conversion methods from {@link User} entity to
 * Data Transfer Objects and vice versa.
 */
@Component
@RequiredArgsConstructor
public class UserConverter {
    /**
     * Converts a {@link User} entity to a response DTO containing the role, and excluding the profilePicture.
     * This response will only be accessible by the admin on the admin panel page.
     *
     * @param user The {@link User} entity to convert
     * @return a {@link UserWithRoleFieldResponseDTO}
     */
    public UserWithRoleFieldResponseDTO toUserWithRoleDTO(User user) {
        return new UserWithRoleFieldResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Converts a {@link User} entity to a response DTO containing the profilePicture, and excluding the role.
     * This response will be accessible by all the users when they look at their profile.
     *
     * @param user The {@link User} entity to convert
     * @return a {@link UserResponseDTO}
     */
    public UserResponseDTO toUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getProfilePicture(),
                user.getEmail()
        );
    }

    /**
     * Converts a {@link UserRegistrationRequestDTO} DTO to a {@link User} entity.
     *
     * @param userDTO The {@link UserRegistrationRequestDTO} DTO to convert
     * @return a {@link User} entity.
     */
    public User toUserEntity(UserRegistrationRequestDTO userDTO) {
        User user = new User();

        user.setFullName(userDTO.fullName());
        user.setEmail(userDTO.email());
        user.setPassword(userDTO.password());

        return user;
    }
}
