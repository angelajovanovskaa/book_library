package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class RequestedBookNotFoundException extends CustomNotFoundException {

    public RequestedBookNotFoundException(UUID id) {
        super("Recommended book with id " + id + " not found");
    }

    public RequestedBookNotFoundException(String isbn) {
        super("Recommended book with isbn " + isbn + " not found");
    }

    public RequestedBookNotFoundException() {
        super("Recommended book not found");
    }
}
