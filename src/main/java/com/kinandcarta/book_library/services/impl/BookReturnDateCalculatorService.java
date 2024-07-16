package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implementation of {@link BookReturnDateCalculatorService}. <p>
 * It contains logic for calculating the anticipated time for reading book and based on that it provides the scheduled return date.
 */

@Service
@RequiredArgsConstructor
public class BookReturnDateCalculatorService {

    private static final int AVERAGE_PAGES_READ_PER_DAY = 25;

    /**
     * This method is used to calculate the scheduledReturnDate for a BookItem when it's borrowed<br>
     * The algorithm get the books totalPages and divides it by the {@code AVERAGE_PAGES_READ_PER_DAY}.
     *
     * @param bookItem Object of the whole BookItem, cannot be {@code null}
     * @return LocalDate for the {@code scheduledReturnDate}.
     */
    public LocalDate calculateReturnDateOfBookItem(BookItem bookItem) {
        Book book = bookItem.getBook();
        int daysNeededToReadABook = Math.ceilDiv(book.getTotalPages(), AVERAGE_PAGES_READ_PER_DAY);

        return LocalDate.now().plusDays(daysNeededToReadABook);
    }
}
