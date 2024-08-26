package com.kinandcarta.book_library.exceptions;

public class RequestedBookStatusException extends CustomBadRequestException{
    public RequestedBookStatusException(String currentBookStatus, String newBookStatus) {
        super("Transition from status " + currentBookStatus + " to status " + newBookStatus + " for requested book is not feasible");
    }
}
