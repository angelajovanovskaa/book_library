package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.services.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {

    private static final int AVERAGE_PAGES_READ_PER_DAY = 25;

    @Override
    public LocalDate calculateReturnDateOfBookItem(BookItem bookItem) {
        int daysNeededToReadABook = Math.ceilDiv(bookItem.getBook().getTotalPages(), AVERAGE_PAGES_READ_PER_DAY);

        return LocalDate.now().plusDays(daysNeededToReadABook);
    }
}
