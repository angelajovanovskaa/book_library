package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.*;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.EmailAlreadyInUseException;
import com.kinandcarta.book_library.exceptions.IncorrectPasswordException;
import com.kinandcarta.book_library.exceptions.InvalidUserCredentialsException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.UserService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
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

    /**
     * <b>This method is used to get all of the registered users.</b>
     * This method will only be accessible by the admin.
     * The list is sorted by roles, so the first accounts are with role ADMIN, and the rest are with role USER.
     *
     * @return UserWithRoleFieldResponseDTO which will contain fullName, email, profilePicture and role.
     */
    @Override
    public List<UserWithRoleFieldResponseDTO> getAllUsers() {
        List<User> users = this.userRepository.findAllByOrderByRoleAsc();

        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    @Override
    public List<UserWithRoleFieldResponseDTO> getAllUsersWithFullName(String fullNameSearchTerm) {
        List<User> users = this.userRepository.findByFullNameContainingIgnoreCaseOrderByRoleAsc(fullNameSearchTerm);

        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    /**
     * <b>This method is used to get all of the information ofr users profile</b>
     * All the users will have access to this method so they can view their profile.
     *
     * @param userId the Id of the user that we are trying to get details for.
     * @return UserWithoutRoleFieldResponseDTO which will contain fullName, email and the profilePicture
     */
    @Override
    public UserWithoutRoleFieldResponseDTO getUserProfile(UUID userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return userConverter.toUserWithoutRoleDTO(user);
    }

    /**
     * <b>This method is used for registering a new user</b>
     * All the users will have access to this method.
     *
     * @param userDTO the DTO will contain fullName, email and password, cannot be @{code null}
     * @return A message that the user with the given email is successfully created.
     * @throws EmailAlreadyInUseException If the email that we are trying to use to create an account is already is use.
     */
    @SneakyThrows
    @Override
    public String registerUser(UserRegistrationRequestDTO userDTO) {
        if (this.userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new EmailAlreadyInUseException(userDTO.email());
        }

        User user = userConverter.toUserEntity(userDTO);
        user.setProfilePicture(getDefaultProfilePicture());

        this.userRepository.save(user);
        return "You have successfully created your account: " + user.getEmail();
    }

    /**
     * <b>This method is used for login in the user in the application</b>
     * All the users will have access to this method.
     *
     * @param userDTO the DTO contains userEmail and Password, cannot be @{code null}
     * @return A message welcoming the user.
     */
    @Override
    public String loginUser(UserLoginRequestDTO userDTO) {
        User user = this.userRepository.findByEmailAndPassword(userDTO.userEmail(), userDTO.userPassword()).orElseThrow(
                InvalidUserCredentialsException::new);

        return "Welcome " + user.getFullName();
    }

    /**
     * <b>This method is used for updating users @{code fullName(name and surname)}, @{code email} and @{code profilePicture}</b>
     * At least one of the 3 attributes needs to not be @{null} so the method can pass.
     * All the users have access to this method for their account.
     *
     * @param userDTO will contain fullName, email and profilePicture
     */
    @Override
    public void updateUserData(UserUpdateDataRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (StringUtils.isNotBlank(userDTO.email())) {
            user.setEmail(userDTO.email());
        }

        if (StringUtils.isNotBlank(userDTO.fullName())) {
            user.setFullName(userDTO.fullName());
        }

        if (userDTO.image().length != 0) {
            user.setProfilePicture(userDTO.image());
        }

        this.userRepository.save(user);
    }

    /**
     * <b>This method is used for updating users role</b>
     * This method will only be accessible by the admin.
     *
     * @param userDTO the DTO contains userId and Role, cannot be @{code null}
     * @return A message that the role for a given user is changed.
     */
    @Override
    public String updateUserRole(UserUpdateRoleRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        user.setRole(userDTO.role());
        this.userRepository.save(user);

        return "You have changed the role for user " + user.getFullName() + " to " + user.getRole();
    }

    /**
     * <b>This method is used to delete the account of an unused user</b>
     * This method will only be accessible by the admin.
     *
     * @param userId the Id of the user that we are trying to delete.
     * @return A message confirming that the delete is successful.
     */
    @Override
    public String deleteAccount(UUID userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        this.userRepository.delete(user);

        return "The account of the user: " + user.getFullName() + "has been successfully deleted";
    }

    @Override
    public String changeUserPassword(UserChangePasswordRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getPassword().equals(userDTO.oldPassword())) {
            throw new IncorrectPasswordException();
        }

        user.setPassword(userDTO.newPassword());
        return "The password has been successfully updated.";
    }

    private byte[] getDefaultProfilePicture() throws IOException {
        InputStream inputStream = User.class.getResourceAsStream("/image/profile-picture.png");
        byte[] imageData = IOUtils.toByteArray(Objects.requireNonNull(inputStream));
        inputStream.close();

        return imageData;
    }
}
