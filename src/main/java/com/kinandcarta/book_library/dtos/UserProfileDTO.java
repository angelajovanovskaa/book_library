package com.kinandcarta.book_library.dtos;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record UserProfileDTO(
        UUID userId,
        String fullName,
        String email,
        byte[] profilePicture
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileDTO that = (UserProfileDTO) o;
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
