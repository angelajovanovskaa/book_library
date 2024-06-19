package com.kinandcarta.book_library.enums;

public enum BookStatus {
    AVAILABLE,         // The book is available for borrowing
    IN_TRANSIT,        // The book is being transported to the library
    ORDERED,           // The book has been ordered but not yet received
    PENDING_PURCHASE,  // The book is pending purchase
}
