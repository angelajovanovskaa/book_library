package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.dtos.ReviewDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class ReviewConverter {

    public ReviewDTO toReviewDTO(Review review) {

        UUID id = review.getId();
        LocalDate date = review.getDate();
        String message = review.getMessage();
        Integer rating = review.getRating();

        Book book = review.getBook();
        String bookISBN = book.getISBN();

        User user = review.getUser();
        String userEmail = user.getEmail();

        return new ReviewDTO(id, date, message, rating, bookISBN, userEmail);

    }

    public Review toReview(ReviewDTO reviewDTO) {

        Review review = new Review();

        review.setId(reviewDTO.id());
        review.setDate(reviewDTO.date());
        review.setMessage(reviewDTO.message());
        review.setRating(reviewDTO.rating());

        return review;
    }
}
