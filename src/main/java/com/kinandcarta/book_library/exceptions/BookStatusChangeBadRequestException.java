package com.kinandcarta.book_library.exceptions;

public class BookStatusChangeBadRequestException extends CustomBadRequestException {
    public BookStatusChangeBadRequestException() {
        super("Book status change failed, invalid new status");
    }
}
