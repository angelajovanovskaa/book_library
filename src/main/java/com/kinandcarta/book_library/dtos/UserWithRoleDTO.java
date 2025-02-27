package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.UserRole;

import java.util.UUID;

public record UserWithRoleDTO(
        UUID userId,
        String fullName,
        String email,
        UserRole role
) {
}