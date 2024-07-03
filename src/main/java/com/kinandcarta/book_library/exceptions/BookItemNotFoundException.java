package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class BookItemNotFoundException extends CustomNotFoundException {
    public BookItemNotFoundException(UUID id) {
        super("The bookItem with id:" + id + " doesn't exist");
    }
}
