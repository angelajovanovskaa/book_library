package com.kinandcarta.book_library.exceptions;

public class BookAlreadyBorrowedByUserException extends CustomUnprocessableEntityException {
    public BookAlreadyBorrowedByUserException(String isbn) {
        super("The user already has an instance borrowed from the book with isbn: " + isbn);
    }
}
