package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class ReviewNotFoundException extends CustomNotFoundException {

    public ReviewNotFoundException(UUID id) {
        super("Review with id " + id + " not found");
    }
}
