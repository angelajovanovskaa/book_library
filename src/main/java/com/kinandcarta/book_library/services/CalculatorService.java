package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.BookItem;

import java.time.LocalDate;

public interface CalculatorService {
    LocalDate calculateReturnDateOfBookItem(BookItem bookItem);
}
