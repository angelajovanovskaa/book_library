package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.UserChangePasswordRequestDTO;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserRegistrationRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateDataRequestDTO;
import com.kinandcarta.book_library.dtos.UserUpdateRoleRequestDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    String updateUserData(UserUpdateDataRequestDTO userDTO);

    UserWithRoleDTO registerUser(UserRegistrationRequestDTO userDTO) throws IOException;

    String updateUserRole(UserUpdateRoleRequestDTO userDTO);

    String loginUser(UserLoginRequestDTO userDTO);

    String deleteAccount(UUID userId);

    String changeUserPassword(UserChangePasswordRequestDTO userDTO);

    List<UserWithRoleDTO> getAllUsers(String officeName);

    List<UserWithRoleDTO> getAllUsersWithFullName(String officeName, String fullNameSearchTerm);

    UserProfileDTO getUserProfile(UUID userId);
}
