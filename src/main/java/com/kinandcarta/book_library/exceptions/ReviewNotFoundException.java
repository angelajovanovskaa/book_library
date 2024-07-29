package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class ReviewNotFoundException extends CustomNotFoundException {

    public ReviewNotFoundException(UUID id) {
        super("Review with id " + id + " not found");
    }

    public ReviewNotFoundException(String email, String isbn) {
        super("Review for user: " + email + " and book with ISBN: " + isbn + " not found");
    }
}
