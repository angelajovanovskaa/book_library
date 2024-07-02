package com.kinandcarta.book_library.exceptions;

public class BookAlreadyBorrowedByUserException extends CustomBadRequestException{
    public BookAlreadyBorrowedByUserException(String ISBN) {
        super("The user already has an instance borrowed from the book with ISBN: "+ISBN);
    }
}
