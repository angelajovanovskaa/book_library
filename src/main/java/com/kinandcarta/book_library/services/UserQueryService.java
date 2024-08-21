package com.kinandcarta.book_library.services;


import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;

import java.util.List;
import java.util.UUID;

public interface UserQueryService {

    List<UserWithRoleDTO> getAllUsers(String officeName);

    List<UserWithRoleDTO> getAllUsersWithFullName(String officeName, String fullNameSearchTerm);

    UserProfileDTO getUserProfile(UUID userId);
}