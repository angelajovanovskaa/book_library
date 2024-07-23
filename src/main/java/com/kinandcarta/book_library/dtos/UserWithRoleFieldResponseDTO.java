package com.kinandcarta.book_library.dtos;

import java.util.UUID;

public record UserWithRoleFieldResponseDTO(
        UUID userId,
        String fullName,
        String email,
        String officeName,
        String role
) {

}
