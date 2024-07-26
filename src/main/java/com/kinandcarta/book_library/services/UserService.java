package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    String updateUserData(UserUpdateDataRequestDTO userDTO);

    String registerUser(UserRegistrationRequestDTO userDTO) throws IOException;

    String updateUserRole(UserUpdateRoleRequestDTO userDTO);

    String loginUser(UserLoginRequestDTO userDTO);

    String deleteAccount(UUID userId);

    String changeUserPassword(UserChangePasswordRequestDTO userDTO);

    List<UserWithRoleFieldResponseDTO> getAllUsers(String officeName);

    List<UserWithRoleFieldResponseDTO> getAllUsersWithFullName(String officeName, String fullNameSearchTerm);

    UserResponseDTO getUserProfile(UUID userId);
}