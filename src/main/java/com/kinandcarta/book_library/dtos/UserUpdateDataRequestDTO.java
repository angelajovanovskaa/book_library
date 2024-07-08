package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record UserUpdateDataRequestDTO(
        @NotBlank
        UUID userId,
        String fullName,
        String email,
        byte[] image
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserUpdateDataRequestDTO that = (UserUpdateDataRequestDTO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(email,
                that.email) && Objects.deepEquals(image, that.image) && Objects.equals(fullName,
                that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fullName, email, Arrays.hashCode(image));
    }

    @Override
    public String toString() {
        return "UserChangePictureRequestDTO{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
