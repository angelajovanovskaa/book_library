package com.kinandcarta.book_library.exceptions;

public class InvalidDataForProfilePictureException extends CustomBadRequestException {
    public InvalidDataForProfilePictureException() {
        super("Please enter e valid data for profilePicture");
    }
}
