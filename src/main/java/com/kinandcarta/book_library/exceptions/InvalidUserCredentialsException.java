package com.kinandcarta.book_library.exceptions;

public class InvalidUserCredentialsException extends CustomNotFoundException {
    public InvalidUserCredentialsException() {
        super("The credentials that you have entered don't match.");
    }
}
