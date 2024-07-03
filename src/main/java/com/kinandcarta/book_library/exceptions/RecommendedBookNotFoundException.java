package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class RecommendedBookNotFoundException extends RuntimeException {

    public RecommendedBookNotFoundException(UUID id) {
        System.out.println("Recommended book with id " + id + " not found");
    }

    public RecommendedBookNotFoundException(String message) {
        System.out.println("Recommended book with isbn/title " + message + " not found");
    }

    public RecommendedBookNotFoundException() {
        System.out.println("Recommended book not found");
    }
}
