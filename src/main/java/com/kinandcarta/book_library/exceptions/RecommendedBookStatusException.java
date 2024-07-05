package com.kinandcarta.book_library.exceptions;

public class RecommendedBookStatusException extends CustomBadRequestException{
    public RecommendedBookStatusException(String from, String to) {
        super("Cannot convert RecommendedBook from status " + from + " to " + to);
    }
}
