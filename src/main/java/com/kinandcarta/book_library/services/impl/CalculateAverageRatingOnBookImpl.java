package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.services.CalculateAverageRatingOnBook;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class is used for implementing calculations used by method in the service logic
 * by the model Review.
 */
@Component
public class CalculateAverageRatingOnBookImpl implements CalculateAverageRatingOnBook {

    public Double getAverageRatingOnBook(List<Integer> reviews) {

        int sumReviews = 0;

        for (Integer value : reviews) {
            sumReviews += value;
        }

        return (double) (sumReviews / reviews.size());
    }
}
