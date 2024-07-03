package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(UUID id) {
        System.out.println("Review with id " + id + " not found");
    }
}
