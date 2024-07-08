package com.kinandcarta.book_library.exceptions;

public class BookNotFoundException extends CustomNotFoundException{
    public BookNotFoundException(String isbn) {
        super("Book with isbn: "+ isbn + " not found");
    }
}
