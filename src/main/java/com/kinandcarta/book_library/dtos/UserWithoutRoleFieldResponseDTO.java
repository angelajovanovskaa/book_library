package com.kinandcarta.book_library.dtos;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record UserWithoutRoleFieldResponseDTO(
        UUID userId,
        String fullName,
        byte[] profilePicture,
        String email
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWithoutRoleFieldResponseDTO that = (UserWithoutRoleFieldResponseDTO) o;
        return Objects.equals(email,
                that.email) && Objects.equals(userId, that.userId) && Objects.equals(fullName,
                that.fullName) && Objects.deepEquals(profilePicture, that.profilePicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fullName, Arrays.hashCode(profilePicture), email);
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", profilePicture=" + Arrays.toString(profilePicture) +
                ", email='" + email + '\'' +
                '}';
    }
}
