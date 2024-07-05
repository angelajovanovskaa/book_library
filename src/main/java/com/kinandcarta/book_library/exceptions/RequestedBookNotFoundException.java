package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class RequestedBookNotFoundException extends CustomNotFoundException {

    public RequestedBookNotFoundException(UUID id) {
        super("Recommended book with id " + id + " not found");
    }

    public RequestedBookNotFoundException(String message) {
        super("Recommended book with isbn/title " + message + " not found");
    }

    public RequestedBookNotFoundException() {
        super("Recommended book not found");
    }
}
