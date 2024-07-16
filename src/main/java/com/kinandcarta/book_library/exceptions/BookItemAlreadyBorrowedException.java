package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class BookItemAlreadyBorrowedException extends CustomBadRequestException {
    public BookItemAlreadyBorrowedException(UUID id) {
        super("The bookItem with id: " + id + " is already booked");
    }
}
