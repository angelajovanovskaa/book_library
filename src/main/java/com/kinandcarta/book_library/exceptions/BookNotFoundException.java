package com.kinandcarta.book_library.exceptions;

public class BookNotFoundException extends RuntimeException{

    public BookNotFoundException(String isbn) {
        System.out.println("Book with isbn " + isbn + " not found");
    }
}
