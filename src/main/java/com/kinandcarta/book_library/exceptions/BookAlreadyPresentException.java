package com.kinandcarta.book_library.exceptions;

public class BookAlreadyPresentException extends CustomNotFoundException{

    public BookAlreadyPresentException(String isbn) {
        super("Book with ISBN " + isbn + " already exists");
    }
}
