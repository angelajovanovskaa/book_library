package com.kinandcarta.book_library.services.impl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookAverageRatingCalculatorImplTest {

    private final BookAverageRatingCalculatorImpl calculateAverageRatingOnBookImpl =
            new BookAverageRatingCalculatorImpl();

    @Test
    void getAverageRatingOnBook_methodIsValid_returnsDouble() {
        //given
        List<Integer> reviews = List.of(1, 2, 3);

        //when
        final double actualResult = calculateAverageRatingOnBookImpl.getAverageRatingOnBook(reviews);

        //then
        assertThat(actualResult).isEqualTo(2);
    }
}