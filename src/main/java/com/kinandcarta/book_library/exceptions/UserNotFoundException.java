package com.kinandcarta.book_library.exceptions;

public class UserNotFoundException extends CustomNotFoundException {

    public UserNotFoundException(String email) {
        super("User with email: " + email + " not found");
    }
}
