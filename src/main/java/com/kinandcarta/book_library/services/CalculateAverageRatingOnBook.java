package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.Review;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  <h2>This class is used for implementing calculations used by method in the service logic
 *  by the model Review.</h2>
 */
@Component
public class CalculateAverageRatingOnBook {

    public Double getAverageRatingOnBook(List<Review> reviews) {

        double sumOfReviews = 0.0;

        for (Review review : reviews){
            sumOfReviews += review.getRating();
        }

        return sumOfReviews/reviews.size();
    }
}
