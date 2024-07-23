package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record UserUpdateDataRequestDTO(
        @NotNull
        UUID userId,
        String fullName,
        String officeName,
        byte[] image
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserUpdateDataRequestDTO that = (UserUpdateDataRequestDTO) o;
        return Objects.equals(userId, that.userId) && Objects.deepEquals(image, that.image) && Objects.equals(fullName,
                that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fullName, Arrays.hashCode(image));
    }

    @Override
    public String toString() {
        return "UserChangePictureRequestDTO{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
