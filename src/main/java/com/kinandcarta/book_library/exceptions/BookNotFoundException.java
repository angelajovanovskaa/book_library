package com.kinandcarta.book_library.exceptions;

public class BookNotFoundException extends CustomNotFoundException {

    public BookNotFoundException(String isbn) {
        super("Book with ISBN: " + isbn + " not found");
    }

    public BookNotFoundException(String isbn, String officeName) {
        super("Book with ISBN: " + isbn + " in office: " + officeName + " not found");
    }
}
