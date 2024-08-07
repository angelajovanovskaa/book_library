package com.kinandcarta.book_library.services.impl;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookReturnDateCalculatorServiceImplTest {

    private final BookReturnDateCalculatorServiceImpl bookReturnDateCalculatorService =
            new BookReturnDateCalculatorServiceImpl();

    @Test
    void calculateReturnDateOfBookItem_theCalculationIsDone_returnsLocalDate() {
        // given
        int totalPages = 123;

        // when
        LocalDate result = bookReturnDateCalculatorService.calculateReturnDateOfBookItem(totalPages);

        // then
        assertThat(result).isEqualTo(LocalDate.now().plusDays(5));
    }
}
