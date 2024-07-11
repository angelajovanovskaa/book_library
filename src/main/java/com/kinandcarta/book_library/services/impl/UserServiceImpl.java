package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.*;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.*;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.UserService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link UserService} that manages the registration and login of user.
 * This service includes methods for operations with users account, like updating and deleting users account.
 * Access controls are specified for different operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final ResourceLoader resourceLoader;

    /**
     * This method is used to get all of the registered users.<br>
     * This method will only be accessible by the admin.
     * The list is sorted by roles, so the first accounts are with role ADMIN, and the rest are with role USER.
     *
     * @return UserWithRoleFieldResponseDTO which will contain fullName, email, profilePicture and role.
     */
    @Override
    public List<UserWithRoleFieldResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAllByOrderByRoleAsc();

        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    @Override
    public List<UserWithRoleFieldResponseDTO> getAllUsersWithFullName(String fullNameSearchTerm) {
        List<User> users = userRepository.findByFullNameContainingIgnoreCaseOrderByRoleAsc(fullNameSearchTerm);

        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    /**
     * This method is used to get all of the information ofr users profile<br>
     * All the users will have access to this method so they can view their profile.
     *
     * @param userId the Id of the user that we are trying to get details for.
     * @return UserWithoutRoleFieldResponseDTO which will contain fullName, email and the profilePicture
     */
    @Override
    public UserWithoutRoleFieldResponseDTO getUserProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return userConverter.toUserWithoutRoleDTO(user);
    }

    /**
     * This method is used for registering a new user<br>
     * All the users will have access to this method.
     *
     * @param userDTO the DTO will contain fullName, email and password, cannot be {@code null}
     * @return A message that the user with the given email is successfully created.
     * @throws EmailAlreadyInUseException If the email that we are trying to use to create an account is already is use.
     */
    @Override
    @Transactional
    public String registerUser(UserRegistrationRequestDTO userDTO) {
        String userEmail = userDTO.email();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            throw new EmailAlreadyInUseException(userEmail);
        }

        User user = userConverter.toUserEntity(userDTO);
        byte[] userProfilePicture = getDefaultProfilePicture();
        user.setProfilePicture(userProfilePicture);

        userRepository.save(user);
        return "You have successfully created the account: " + userEmail;
    }

    /**
     * This method is used for login in the user in the application<br>
     * All the users will have access to this method.
     *
     * @param userDTO the DTO contains userEmail and Password, cannot be {@code null}
     * @return A message welcoming the user.
     */
    @Override
    public String loginUser(UserLoginRequestDTO userDTO) {
        User user = userRepository.findByEmailAndPassword(userDTO.userEmail(), userDTO.userPassword()).orElseThrow(
                InvalidUserCredentialsException::new);

        return "Welcome " + user.getFullName();
    }

    /**
     * This method is used for updating users {@code fullName(name and surname)} and {@code profilePicture}<br>
     * At least one of the two attributes needs to not be {@code null} so the method can pass.
     * All the users have access to this method for their account.
     *
     * @param userDTO will contain fullName and profilePicture
     */
    @Override
    @Transactional
    public void updateUserData(UserUpdateDataRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (StringUtils.isNotBlank(userDTO.fullName())) {
            user.setFullName(userDTO.fullName());
        }

        if (userDTO.image().length != 0) {
            user.setProfilePicture(userDTO.image());
        }

        userRepository.save(user);
    }

    /**
     * This method is used for updating users role<br>
     * This method will only be accessible by the admin.
     *
     * @param userDTO the DTO contains userId and Role, cannot be {@code null}
     * @return A message that the role for a given user is changed.
     */
    @Override
    @Transactional
    public String updateUserRole(UserUpdateRoleRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        user.setRole(userDTO.role());
        userRepository.save(user);

        return "You have changed the role for user " + user.getFullName() + " to " + user.getRole();
    }

    /**
     * This method is used to delete the account of an unused user<br>
     * This method will only be accessible by the admin.
     *
     * @param userId the Id of the user that we are trying to delete.
     * @return A message confirming that the delete operation is successful.
     */
    @Override
    @Transactional
    public String deleteAccount(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);

        return "The account of the user: " + user.getFullName() + "has been successfully deleted";
    }

    /**
     * This method is used to change the password for the users account<br>
     * All the users have access to this method for their account.
     *
     * @param userDTO the DTO contains userId, oldPassword and newPassword cannot be {@code null}
     * @return A string message confirming that the password has been successfully changed.
     */
    @Override
    @Transactional
    public String changeUserPassword(UserChangePasswordRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getPassword().equals(userDTO.oldPassword())) {
            throw new IncorrectPasswordException();
        }

        user.setPassword(userDTO.newPassword());
        userRepository.save(user);

        return "The password has been successfully updated.";
    }

    private byte[] getDefaultProfilePicture() {
        byte[] imageData;

        Resource resource = resourceLoader.getResource("classpath:image/profile-picture.png");
        try {
            imageData = resource.getContentAsByteArray();
        } catch (IOException e) {
            throw new InvalidDataForProfilePictureException();
        }

        return imageData;
    }
}
