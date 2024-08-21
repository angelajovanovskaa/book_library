package com.kinandcarta.book_library.services;


import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;

import java.util.List;
import java.util.UUID;

public interface UserQueryService {

    List<UserWithRoleDTO> getUsers(String officeName);

    List<UserWithRoleDTO> getUsersWithFullName(String officeName, String fullNameSearchTerm);

    UserProfileDTO getUserProfile(UUID userId);
}