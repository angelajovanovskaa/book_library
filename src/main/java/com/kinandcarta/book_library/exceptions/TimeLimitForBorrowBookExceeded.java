package com.kinandcarta.book_library.exceptions;

public class TimeLimitForBorrowBookExceeded extends CustomBadRequestException {
    public TimeLimitForBorrowBookExceeded(int number) {
        super("You can't borrow the same book before the period of "
                + number + " days have passed after your last borrowing.");
    }
}
