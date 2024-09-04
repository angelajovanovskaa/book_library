package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link UserQueryService} that handles query operations for user data.<br>
 * This service includes methods for retrieving user views, such as getting all users, filtering users by name, and
 * fetching user profiles.<br>
 * All operations in this service correspond to GET HTTP methods and are primarily used for fetching and displaying
 * user information.
 * Access controls are specified for different query operations.
 */
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    /**
     * This method is used to get all the registered users.<br>
     * This method will only be accessible by the admin.
     * The list is sorted by roles, so the first accounts are with role ADMIN, and the rest are with role USER.
     *
     * @param officeName the name of the office where the user searching belongs.
     * @return A list of {@link UserWithRoleDTO}
     */
    @Override
    public List<UserWithRoleDTO> getUsers(String officeName) {
        List<User> users = userRepository.findAll(officeName);

        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    /**
     * This method is used to filter the registered users by their fullName.
     * This method will only be accessible by the admin.
     * The list is sorted by roles, so the first accounts are with role ADMIN, and the rest are with role USER.
     *
     * @param officeName         the name of the office where the user searching belongs.
     * @param fullNameSearchTerm String value for the fullName of User.
     * @return A list of {@link UserWithRoleDTO}
     */
    @Override
    public List<UserWithRoleDTO> getUsersWithFullName(String officeName, String fullNameSearchTerm) {
        List<User> users = userRepository.findByOfficeNameAndFullNameContaining(officeName, fullNameSearchTerm);


        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    /**
     * This method is used to get all the information for users profile<br>
     * All the users will have access to this method, so they can view their profile.
     *
     * @param userId UUID for the id of the user that we are trying to get details for.
     * @return {@link UserProfileDTO}
     */
    @Override
    public UserProfileDTO getUserProfile(UUID userId) {
        User user = userRepository.getReferenceById(userId);

        return userConverter.toUserProfileDTO(user);
    }
}