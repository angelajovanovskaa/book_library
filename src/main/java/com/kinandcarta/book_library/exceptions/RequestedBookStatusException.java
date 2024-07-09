package com.kinandcarta.book_library.exceptions;

public class RequestedBookStatusException extends CustomBadRequestException{
    public RequestedBookStatusException(String from, String to) {
        super("Cannot convert RecommendedBook from status " + from + " to status " + to);
    }
}
