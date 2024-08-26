package com.kinandcarta.book_library.exceptions;

public class LimitReachedForBorrowedBooksException extends CustomUnprocessableEntityException {
    public LimitReachedForBorrowedBooksException(int number) {
        super("You have reached the maximum number of borrowed books. " + number + " books borrowed already.");
    }
}
