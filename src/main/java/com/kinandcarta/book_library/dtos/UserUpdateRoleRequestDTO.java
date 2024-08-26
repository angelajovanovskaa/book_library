package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.UserRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateRoleRequestDTO(
        @NotNull
        UUID userId,
        @NotNull
        UserRole role
) {
}
