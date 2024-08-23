package com.kinandcarta.book_library.exceptions;

public class DuplicateReviewException extends CustomUnprocessableEntityException{
    public DuplicateReviewException() {
        super("The user has already left a review for this book.");
    }
}
