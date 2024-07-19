package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.BookItem;

import java.time.LocalDate;

public interface BookReturnDateCalculatorService {
    LocalDate calculateReturnDateOfBookItem(BookItem bookItem);
}
