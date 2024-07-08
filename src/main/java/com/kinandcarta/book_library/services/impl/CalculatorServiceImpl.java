package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.services.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implementation of {@link CalculatorService}. It has different calculation algorithms
 * used in the service logic for all the other services.
 */

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {

    private static final int AVERAGE_PAGES_READ_PER_DAY = 25;

    /**
     * <b>This method is used to calculate the scheduledReturnDate for a BookItem when it's borrowed</b><br>
     * The algorithm get the books totalPages and divides it by the {@code AVERAGE_PAGES_READ_PER_DAY}.
     *
     * @param bookItem Object of the whole BookItem, cannot be {@code null}
     * @return LocalDate for the {@code scheduledReturnDate}.
     */
    @Override
    public LocalDate calculateReturnDateOfBookItem(BookItem bookItem) {
        int daysNeededToReadABook = Math.ceilDiv(bookItem.getBook().getTotalPages(), AVERAGE_PAGES_READ_PER_DAY);

        return LocalDate.now().plusDays(daysNeededToReadABook);
    }
}
