package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class UserNotFoundException extends CustomNotFoundException {

    public UserNotFoundException(UUID id) {
        super("User with id: " + id + " not found");
    }
}
