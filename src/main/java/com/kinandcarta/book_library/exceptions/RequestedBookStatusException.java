package com.kinandcarta.book_library.exceptions;

public class RequestedBookStatusException extends CustomBadRequestException{
    public RequestedBookStatusException(String currentStatus, String newStatus) {
        super("Transition from status " + currentStatus + " to status " + newStatus + " for requested book is not feasible");
    }
}
