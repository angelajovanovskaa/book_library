package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class BookItemIsNotBorrowedException extends CustomBadRequestException {
    public BookItemIsNotBorrowedException(UUID bookItemId) {
        super("The bookItem with id " + bookItemId + " can't be returned because it is not borrowed.");
    }
}
