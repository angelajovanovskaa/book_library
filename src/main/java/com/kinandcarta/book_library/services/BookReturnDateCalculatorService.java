package com.kinandcarta.book_library.services;

import java.time.LocalDate;

public interface BookReturnDateCalculatorService {
    LocalDate calculateReturnDateOfBookItem(int totalPages);
}
