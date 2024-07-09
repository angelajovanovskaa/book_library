package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 *  <h1>This class is used for implementing calculations used by method in the service logic
 *  by the model Review.</h1>
 *  <h6><i>Methods:</i></h6>
 *  <un>
 *      <li>getAverageRatingOnBook - calculates the average rating given by employees who borrowed the book.</li>
 *  </un>
 */
@Component
public class CalculateAverageReviewRatingOnBook {

    public Double getAverageRatingOnBook(List<Review> reviews) {

        double sumOfReviews = 0.0;

        for (Review review : reviews){
            sumOfReviews += review.getRating();
        }

        return sumOfReviews/reviews.size();
    }
}
