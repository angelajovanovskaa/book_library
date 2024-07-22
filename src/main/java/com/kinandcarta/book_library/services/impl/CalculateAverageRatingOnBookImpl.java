package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.services.CalculateAverageRatingOnBook;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Performs calculation to provide an average rating (of type double) for given {@link Book} object.
 */
@Component
public class CalculateAverageRatingOnBookImpl implements CalculateAverageRatingOnBook {

    /**
     * Calculates average from List of Integers ({@link Review} ratings)
     * <hr>
     *
     * @param reviews Type: List of Integers
     * @return double
     */
    public double getAverageRatingOnBook(List<Integer> reviews) {

        double sumReviews = 0;

        for (Integer value : reviews) {
            sumReviews += value;
        }

        return sumReviews / reviews.size();
    }
}
