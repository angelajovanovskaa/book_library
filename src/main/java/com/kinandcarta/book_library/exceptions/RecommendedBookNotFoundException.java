package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class RecommendedBookNotFoundException extends CustomNotFoundException {

    public RecommendedBookNotFoundException(UUID id) {
        super("Recommended book with id " + id + " not found");
    }

    public RecommendedBookNotFoundException(String message) {
        super("Recommended book with isbn/title " + message + " not found");
    }

    public RecommendedBookNotFoundException() {
        super("Recommended book not found");
    }
}
