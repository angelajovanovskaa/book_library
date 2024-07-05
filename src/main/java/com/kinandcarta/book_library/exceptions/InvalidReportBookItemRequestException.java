package com.kinandcarta.book_library.exceptions;

public class InvalidReportBookItemRequestException extends CustomBadRequestException{
    public InvalidReportBookItemRequestException() {
        super("Invalid request for returning a bookItem. The id field needs to be populated.");
    }
}
