package com.kinandcarta.book_library.exceptions;

public class EmailAlreadyInUseException extends CustomBadRequestException {
    public EmailAlreadyInUseException(String email) {
        super("The email: " + email + " is already in use.");
    }
}
