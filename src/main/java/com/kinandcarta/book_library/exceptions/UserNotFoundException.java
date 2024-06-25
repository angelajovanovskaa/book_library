package com.kinandcarta.book_library.exceptions;

public class UserNotFoundException extends CustomNotFoundException {
    public UserNotFoundException(Long id) {
        super("The user with id:" + id + " doesn't exist");
    }
}
