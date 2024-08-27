package com.kinandcarta.book_library.exceptions;

public class DuplicateUserReviewException extends CustomUnprocessableEntityException{
    public DuplicateUserReviewException() {
        super("The user has already left a review for this book.");
    }
}
