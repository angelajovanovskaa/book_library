package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.services.CalculateAverageRatingOnBook;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Performs calculation to provide a average rating (of type double) for given Book object.
 */
@Component
public class CalculateAverageRatingOnBookImpl implements CalculateAverageRatingOnBook {
    
    public double getAverageRatingOnBook(List<Integer> reviews) {

        double sumReviews = 0;

        for (Integer value : reviews) {
            sumReviews += value;
        }

        return sumReviews / reviews.size();
    }
}
