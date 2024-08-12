package com.kinandcarta.book_library.exceptions;

public class OfficeNotFoundException extends CustomNotFoundException {

    public OfficeNotFoundException(String officeName) {
        super("Office with name: " + officeName + " not found");
    }
}
