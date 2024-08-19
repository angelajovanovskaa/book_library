package com.kinandcarta.book_library.dtos;

import java.util.UUID;

public record UserWithRoleDTO(
        UUID userId,
        String fullName,
        String email,
        String role
) {
}