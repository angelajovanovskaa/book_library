package com.kinandcarta.book_library.enums;

public enum BookState {
    AVAILABLE,       // The book is available for borrowing
    RESERVED,        // The book has been reserved by a user
    BORROWED,        // The book is currently borrowed and checked out by a user
    OVERDUE,         // The book is overdue for return
    RETURNED,        // The book has been returned and is being processed
    LOST,            // The book has been reported as lost
    DAMAGED,         // The book has been returned in a damaged condition
    UNDER_REPAIR     // The book is under repair
}
