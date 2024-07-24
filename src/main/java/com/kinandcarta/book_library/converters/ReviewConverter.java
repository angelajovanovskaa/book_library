package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.ReviewDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Performs conversion of object from type {@link Review} to {@link ReviewDTO} and vice versa.
 */
@Component
public class ReviewConverter {

    /**
     * Converts a {@link Review} entity to a {@link ReviewDTO} object.
     *
     * @param review The {@link Review} entity to convert
     * @return a {@link ReviewDTO}
     */
    public ReviewDTO toReviewDTO(Review review) {

        UUID id = review.getId();
        LocalDate date = review.getDate();
        String message = review.getMessage();
        Integer rating = review.getRating();

        Book book = review.getBook();
        String bookISBN = book.getIsbn();

        User user = review.getUser();
        String userEmail = user.getEmail();

        return new ReviewDTO(id, date, message, rating, bookISBN, userEmail);
    }

    /**
     * Converts a {@link ReviewDTO} entity to a {@link Review} object.
     *
     * @param reviewDTO The {@link ReviewDTO} entity to convert
     * @return a {@link Review}
     */
    public Review toReview(ReviewDTO reviewDTO) {

        Review review = new Review();

        review.setId(reviewDTO.id());
        review.setDate(reviewDTO.date());
        review.setMessage(reviewDTO.message());
        review.setRating(reviewDTO.rating());

        return review;
    }
}
