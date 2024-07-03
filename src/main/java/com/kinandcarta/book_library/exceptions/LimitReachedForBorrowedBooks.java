package com.kinandcarta.book_library.exceptions;

public class LimitReachedForBorrowedBooks extends CustomBadRequestException {
    public LimitReachedForBorrowedBooks(Long number) {
        super("You have reached the maximum number of borrowed books. " + number + " books borrowed already.");
    }
}
