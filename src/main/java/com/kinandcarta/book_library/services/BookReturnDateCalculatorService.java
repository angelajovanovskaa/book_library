package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.Book;

import java.time.LocalDate;

public interface BookReturnDateCalculatorService {
    LocalDate calculateReturnDateOfBookItem(Book book);
}
