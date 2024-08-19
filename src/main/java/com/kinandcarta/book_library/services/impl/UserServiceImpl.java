package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.UserConverter;
import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleFieldResponseDTO;
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
 * Includes methods for retrieving views of all users and their filtering.
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
     * @param fullNameSearchTerm String value for the fullName of User.
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

    /**
     * This method is used for registering a new user<br>
     * All the users will have access to this method.
     *
     * @param userDTO the DTO where we have data needed for registering new account.
     * @return {@link UserWithRoleFieldResponseDTO}
     * @throws EmailAlreadyInUseException If the email that we are trying to use to create an account is already is use.
     */
    @Override
    @Transactional
    public UserWithRoleFieldResponseDTO registerUser(UserRegistrationRequestDTO userDTO) throws IOException {
        String userEmail = userDTO.email();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            throw new EmailAlreadyInUseException(userEmail);
        }

        User user = userConverter.toUserEntity(userDTO);

        Office office = officeRepository.getReferenceById(userDTO.officeName());
        user.setOffice(office);

        byte[] userProfilePicture = getDefaultProfilePicture();
        user.setProfilePicture(userProfilePicture);

        userRepository.save(user);
        return userConverter.toUserWithRoleDTO(user);
    }

    /**
     * This method is used for login in the user in the application<br>
     * All the users will have access to this method.
     *
     * @param userDTO the DTO where we have data needed for login.
     * @return {@code fullName} of the logged in {@link User}.
     */
    @Override
    public String loginUser(UserLoginRequestDTO userDTO) {
        User user = userRepository.findByEmailAndPassword(userDTO.userEmail(), userDTO.userPassword()).orElseThrow(
                InvalidUserCredentialsException::new);

        return user.getFullName();
    }

    /**
     * This method is used for updating {@link User} information<br>
     * Existing User data will be updated only if there's value present for the corresponding data in
     * UserUpdateDataRequestDTO. For instance, if a certain field has null/empty value in UserUpdateDataRequestDTO,
     * it will not be updated.<br>
     * All the users have access to this method for their account.
     *
     * @param userDTO the DTO where we have data needed for updating the {@link User} information.
     * @return A message for the user that the update was successful.
     */
    @Override
    @Transactional
    public String updateUserData(UserUpdateDataRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = userRepository.getReferenceById(userId);

        if (StringUtils.isNotBlank(userDTO.fullName())) {
            user.setFullName(userDTO.fullName());
        }

        byte[] image = userDTO.image();

        if (image != null && image.length != 0) {
            user.setProfilePicture(image);
        }

        userRepository.save(user);
        return UserResponseMessages.USER_DATA_UPDATED_RESPONSE;
    }

    /**
     * This method is used for updating users role<br>
     * This method will only be accessible by the admin.
     *
     * @param userDTO the DTO where we have data needed for updating {@link User} role.
     * @return A message for the user that the role is successfully changed.
     */
    @Override
    @Transactional
    public String updateUserRole(UserUpdateRoleRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = userRepository.getReferenceById(userId);

        user.setRole(userDTO.role());
        userRepository.save(user);

        return UserResponseMessages.USER_ROLE_UPDATED_RESPONSE;
    }

    /**
     * This method is used to delete the account of an unused user<br>
     * This method will only be accessible by the admin.
     *
     * @param userId UUID for the id of the user that we are trying to delete.
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
     * @param userDTO the DTO where we have data needed for changing the {@link User} password.
     * @return A string message confirming that the password has been successfully changed.
     */
    @Override
    @Transactional
    public String changeUserPassword(UserChangePasswordRequestDTO userDTO) {
        UUID userId = userDTO.userId();
        User user = userRepository.getReferenceById(userId);

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
