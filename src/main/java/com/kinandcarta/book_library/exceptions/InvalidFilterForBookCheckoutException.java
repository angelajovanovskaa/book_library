package com.kinandcarta.book_library.exceptions;

public class InvalidFilterForBookCheckoutException extends CustomBadRequestException {
    public InvalidFilterForBookCheckoutException() {
        super("Invalid filter request for bookCheckout. You need to have userId or bookIsbn populate, if not use the method to get all records.");
    }
}
