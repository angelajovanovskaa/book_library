package com.kinandcarta.book_library.exceptions;

public class IncorrectPasswordException extends CustomUnprocessableEntityException {
    public IncorrectPasswordException() {
        super("The password that you have entered is incorrect.");
    }
}
