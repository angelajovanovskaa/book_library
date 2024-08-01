package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class RequestedBookNotFoundException extends CustomNotFoundException {

    public RequestedBookNotFoundException(UUID id) {
        super("RequestedBook with id " + id + " not found");
    }

    public RequestedBookNotFoundException(String isbn, String officeName) {
        super("RequestedBook with ISBN " + isbn + " for office " + officeName + " not found");
    }
}
