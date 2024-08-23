package com.kinandcarta.book_library.exceptions;

public class BookStatusChangeBadRequestException extends CustomBadRequestException {
    public BookStatusChangeBadRequestException() {
        super("Book status change failed, cannot use IN_STOCK as new book status");
    }
}
