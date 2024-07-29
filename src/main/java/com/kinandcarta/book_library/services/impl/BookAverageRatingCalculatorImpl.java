package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.services.BookAverageRatingCalculator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Performs calculation to provide an average rating (of type double) for given {@link Book} object.
 */
@Component
public class BookAverageRatingCalculatorImpl implements BookAverageRatingCalculator {

    /**
     * Calculates average rating using all the ratings of type Integer (from object of type {@link Review})
     * for given {@link Book} object.
     * <hr>
     *
     * @param reviewRatingIntegers Type: List of Integers
     * @return double
     */
    public double getAverageRatingOnBook(List<Integer> reviewRatingIntegers) {

        double sumReviews = 0;

        for (Integer value : reviewRatingIntegers) {
            sumReviews += value;
        }

        return sumReviews / reviewRatingIntegers.size();
    }
}
