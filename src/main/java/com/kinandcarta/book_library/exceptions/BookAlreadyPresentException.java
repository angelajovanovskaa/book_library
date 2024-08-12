package com.kinandcarta.book_library.exceptions;

public class BookAlreadyPresentException extends CustomNotFoundException {

    public BookAlreadyPresentException(String isbn, String officeName) {
        super("Book with ISBN: " + isbn + " in office: " + officeName + " already exists");
    }
}
