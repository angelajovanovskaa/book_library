package com.kinandcarta.book_library.enums;

public enum BookItemState {
    AVAILABLE,       // The book is available for borrowing
    BORROWED,        // The book is currently borrowed and checked out by a user
    LOST,            // The book has been reported as lost
    DAMAGED         // The book has been returned in a damaged condition
}
