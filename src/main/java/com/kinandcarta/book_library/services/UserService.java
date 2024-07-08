package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void updateUserData(UserUpdateDataRequestDTO userDTO);

    String registerUser(UserRegistrationRequestDTO userDTO);

    String updateUserRole(UserUpdateRoleRequestDTO userDTO);

    String loginUser(UserLoginRequestDTO userDTO);

    String deleteAccount(UUID userId);

    String changeUserPassword(UserChangePasswordRequestDTO userDTO);

    List<UserWithRoleFieldResponseDTO> getAllUsers();

    List<UserWithRoleFieldResponseDTO> getAllUsersWithFullName(String fullNameSearchTerm);

    UserWithoutRoleFieldResponseDTO getUserProfile(UUID userId);
}
