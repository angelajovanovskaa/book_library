package com.kinandcarta.book_library.exceptions;

public class EntitiesInDifferentOfficesException extends CustomUnprocessableEntityException {
    public EntitiesInDifferentOfficesException() {
        super("You can't borrow a book from a different office!");
    }
}
