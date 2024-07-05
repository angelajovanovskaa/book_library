package com.kinandcarta.book_library.exceptions;

public class InvalidReturnBookItemRequestException extends CustomBadRequestException {
    public InvalidReturnBookItemRequestException() {
        super("Invalid request for returning a bookItem. The id field needs to be populated.");
    }
}
