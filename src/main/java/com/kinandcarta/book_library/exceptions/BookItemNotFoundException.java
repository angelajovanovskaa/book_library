package com.kinandcarta.book_library.exceptions;

import java.util.UUID;

public class BookItemNotFoundException extends CustomNotFoundException {
    public BookItemNotFoundException(UUID id) {
        super("The bookItem with barcode: " + id + " doesn't exist");
    }
}