package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.*;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.EmailAlreadyInUseException;
import com.kinandcarta.book_library.exceptions.IncorrectPasswordException;
import com.kinandcarta.book_library.exceptions.InvalidUserCredentialsException;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.UserService;
import com.kinandcarta.book_library.utils.UserResponseMessages;
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
 * Implementation of {@link UserService} that manages the registration and login of user.<br>
 * This service includes methods for operations with users account, like updating and deleting users account.
 * Access controls are specified for different operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String IMAGE_PATH = "classpath:image/profile-picture.png";

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final ResourceLoader resourceLoader;
    private final OfficeRepository officeRepository;

    /**
     * This method is used to get all the registered users.<br>
     * This method will only be accessible by the admin.
     * The list is sorted by roles, so the first accounts are with role ADMIN, and the rest are with role USER.
     *
     * @param officeName the name of the office where the user searching belongs.
     * @return A list of {@link UserWithRoleFieldResponseDTO}
     */
    @Override
    public List<UserWithRoleFieldResponseDTO> getAllUsers(String officeName) {
        List<User> users = userRepository.findAllByOffice_NameOrderByRoleAsc(officeName);

        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    /**
     * This method is used to filter the registered users by their fullName
     * This method will only be accessible by the admin.
     * The list is sorted by roles, so the first accounts are with role ADMIN, and the rest are with role USER.
     *
     * @param officeName         the name of the office where the user searching belongs.
     * @param fullNameSearchTerm String value for the fullName of User, cannot be {@code null}
     * @return A list of {@link UserWithRoleFieldResponseDTO}
     */
    @Override
    public List<UserWithRoleFieldResponseDTO> getAllUsersWithFullName(String officeName, String fullNameSearchTerm) {
        List<User> users =
                userRepository.findByOffice_NameAndFullNameContainingIgnoreCaseOrderByRoleAsc(officeName,
                        fullNameSearchTerm);

        return users.stream().map(userConverter::toUserWithRoleDTO).toList();
    }

    /**
     * This method is used to get all the information ofr users profile<br>
     * All the users will have access to this method, so they can view their profile.
     *
     * @param userId the Id of the user that we are trying to get details for.
     * @return {@link UserResponseDTO}
     */
    @Override
    public UserResponseDTO getUserProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();

        return userConverter.toUserResponseDTO(user);
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
    public String registerUser(UserRegistrationRequestDTO userDTO) throws IOException {
        String userEmail = userDTO.email();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            throw new EmailAlreadyInUseException(userEmail);
        }

        User user = userConverter.toUserEntity(userDTO);

        Office office = officeRepository.findById(userDTO.officeName()).orElseThrow();
        user.setOffice(office);

        byte[] userProfilePicture = getDefaultProfilePicture();
        user.setProfilePicture(userProfilePicture);

        userRepository.save(user);
        return UserResponseMessages.USER_REGISTERED_RESPONSE;
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

        return user.getFullName();
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
    public String updateUserData(UserUpdateDataRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = userRepository.findById(userId).orElseThrow();

        if (StringUtils.isNotBlank(userDTO.fullName())) {
            user.setFullName(userDTO.fullName());
        }

        if (userDTO.image().length != 0) {
            user.setProfilePicture(userDTO.image());
        }

        if (StringUtils.isNotBlank(userDTO.officeName())) {
            Office office = officeRepository.findById(userDTO.officeName()).orElseThrow();
            user.setOffice(office);
        }

        userRepository.save(user);
        return UserResponseMessages.USER_DATA_UPDATED_RESPONSE;
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
        User user = userRepository.findById(userId).orElseThrow();

        user.setRole(userDTO.role());
        userRepository.save(user);

        return UserResponseMessages.USER_ROLE_UPDATED_RESPONSE;
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
        userRepository.deleteById(userId);

        return UserResponseMessages.USER_DELETED_RESPONSE;
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
        User user = userRepository.findById(userId).orElseThrow();

        if (!user.getPassword().equals(userDTO.oldPassword())) {
            throw new IncorrectPasswordException();
        }

        user.setPassword(userDTO.newPassword());
        userRepository.save(user);

        return UserResponseMessages.USER_PASSWORD_UPDATED_RESPONSE;
    }

    private byte[] getDefaultProfilePicture() throws IOException {
        Resource resource = resourceLoader.getResource(IMAGE_PATH);

        return resource.getContentAsByteArray();
    }
}
