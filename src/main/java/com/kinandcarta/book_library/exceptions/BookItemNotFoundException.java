package com.kinandcarta.book_library.exceptions;

public class BookItemNotFoundException extends CustomNotFoundException{
    public BookItemNotFoundException(Long id) {
        super("The bookItem with id:" + id +" doesn't exist");
    }
}
