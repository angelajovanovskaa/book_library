package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookReturnDateCalculatorServiceImplTest {

    private final BookReturnDateCalculatorServiceImpl bookReturnDateCalculatorService =
            new BookReturnDateCalculatorServiceImpl();

    @Test
    void calculateReturnDateOfBookItem_theCalculationIsDone_returnsLocalDate() {
        // given

        // when
        LocalDate actualResult =
                bookReturnDateCalculatorService.calculateReturnDateOfBookItem(BookTestData.BOOK_TOTAL_PAGES);

        // then
        assertThat(actualResult).isEqualTo(SharedTestData.FUTURE_DATE);
    }
}
