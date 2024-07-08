package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fullName;

    private byte[] profilePicture = getDefaultProfilePicture();

    private String email;

    private String role = "USER";

    private String password;

    private static byte[] getDefaultProfilePicture() {
        try {
            InputStream inputStream = User.class.getResourceAsStream("/image/profile-picture.png");
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
