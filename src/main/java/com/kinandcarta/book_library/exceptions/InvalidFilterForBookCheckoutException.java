package com.kinandcarta.book_library.exceptions;

public class InvalidFilterForBookCheckoutException extends CustomBadRequestException {
    public InvalidFilterForBookCheckoutException() {
        super("Invalid filter request for bookCheckout. You need to populate the fields u want to filter by.");
    }
}
