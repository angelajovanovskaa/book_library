package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * <ul>
 *     <li>Performs conversion of object from type {@link Review} to {@link ReviewResponseDTO}.</li>
 *     <li>Performs conversion of object from type {@link ReviewRequestDTO} to {@link Review}.</li>
 * </ul>
 */
@Component
public class ReviewConverter {

    /**
     * Converts a {@link Review} entity to a {@link ReviewResponseDTO} entity.
     *
     * @param review {@link Review} entity to convert
     * @return {@link ReviewResponseDTO} converted entity
     */
    public ReviewResponseDTO toReviewResponseDTO(Review review) {
        UUID reviewId = review.getId();

        Book book = review.getBook();
        String bookISBN = book.getIsbn();

        User user = review.getUser();
        String userEmail = user.getEmail();

        LocalDate date = review.getDate();
        String message = review.getMessage();
        Integer rating = review.getRating();

        return new ReviewResponseDTO(reviewId, bookISBN, userEmail, date, message, rating);
    }

    /**
     * Converts a {@link ReviewRequestDTO} entity to a {@link Review} entity.
     *
     * @param reviewRequestDTO {@link ReviewRequestDTO} entity to convert
     * @return {@link Review} converted entity
     */
    public Review toReview(ReviewRequestDTO reviewRequestDTO) {
        Review review = new Review();

        review.setMessage(reviewRequestDTO.message());
        review.setRating(reviewRequestDTO.rating());

        return review;
    }
}
