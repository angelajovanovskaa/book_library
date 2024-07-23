package com.kinandcarta.book_library.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookAverageRatingCalculatorImplTest {

    @InjectMocks
    private BookAverageRatingCalculatorImpl calculateAverageRatingOnBookImpl;

    @Test
    void getAverageRatingOnBook_methodIsValid_returnsDouble() {

        List<Integer> reviews = List.of(1, 2, 3);

        final double actualResult = calculateAverageRatingOnBookImpl.getAverageRatingOnBook(reviews);

        assertThat(actualResult).isEqualTo(2);
    }
}