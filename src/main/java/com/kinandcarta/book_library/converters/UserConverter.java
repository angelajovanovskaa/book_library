package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for providing conversion methods from {@link User} entity to
 * Data Transfer Objects and vice versa.
 */
@Component
public class UserConverter {

    /**
     * Converts a {@link User} entity to a response DTO containing the role.
     * This response will only be accessible by the admin on the admin panel page.
     *
     * @param user The {@link User} entity to convert
     * @return a {@link UserWithRoleDTO}
     */
    public UserWithRoleDTO toUserWithRoleDTO(User user) {
        return new UserWithRoleDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Converts a {@link User} entity to a response DTO containing the profilePicture.
     * This response will be accessible by all the users when they look at their profile.
     *
     * @param user The {@link User} entity to convert
     * @return a {@link UserProfileDTO}
     */
    public UserProfileDTO toUserProfileDTO(User user) {

        return new UserProfileDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getProfilePicture()
        );
    }

    /**
     * Converts a {@link UserRegistrationRequestDTO} DTO to a {@link User} entity.
     *
     * @param userDTO The {@link UserRegistrationRequestDTO} DTO to convert
     * @return a {@link User} entity.
     */
    public User toUserEntity(UserRegistrationRequestDTO userDTO, String password, Office office,
                             byte[] profilePicture) {
        User user = new User();

        user.setFullName(userDTO.fullName());
        user.setEmail(userDTO.email());
        user.setPassword(password);
        user.setOffice(office);
        user.setProfilePicture(profilePicture);

        return user;
    }
}
