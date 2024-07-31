package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.services.ReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * This service provides methods for querying {@link Review} data.
 * <p>
 * This service handles retrieving all reviews, retrieving a review by its ID,
 * retrieving reviews for a book by its ISBN, and getting the top 3 reviews for a book.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;

    /**
     * Retrieves all reviews available in the system.
     * <p>
     * This method fetches all reviews from the repository and converts them to {@link ReviewResponseDTO} objects.
     * </p>
     *
     * @return List of {@link ReviewResponseDTO} representing all reviews.
     */
    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(reviewConverter::toReviewResponseDTO)
                .toList();
    }

    /**
     * Retrieves a specific review by its unique ID.
     * <p>
     * This method finds a review by its ID and converts it to a {@link ReviewResponseDTO} object.
     * If no review is found with the provided ID, a {@link RuntimeException} is thrown.
     * </p>
     *
     * @param reviewId ID of the review to retrieve.
     * @return {@link ReviewResponseDTO} representing the review with the specified ID.
     * @throws RuntimeException if no review is found with the given ID.
     */
    @Override
    public ReviewResponseDTO getReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        return reviewConverter.toReviewResponseDTO(review);
    }

    /**
     * Retrieves reviews for a specific book identified by its ISBN.
     * <p>
     * This method fetches all reviews for a book with the given ISBN and converts them to {@link ReviewResponseDTO}
     * objects.
     * </p>
     *
     * @param isbn       ISBN of the book for which reviews are to be retrieved.
     * @param officeName Name of hte {@link Office} where the {@link Book} belongs.
     * @return List of {@link ReviewResponseDTO} representing reviews for the book with the specified ISBN.
     */
    @Override
    public List<ReviewResponseDTO> getAllReviewsByBookIsbnAndByOfficeName(String isbn, String officeName) {
        List<Review> reviews = reviewRepository.findAllByBookIsbnAndOfficeName(isbn, officeName);
        return reviews.stream()
                .map(reviewConverter::toReviewResponseDTO)
                .toList();
    }

    /**
     * Retrieves the top 3 reviews for a book identified by its ISBN, sorted by rating in descending order.
     * <p>
     * This method fetches the top 3 reviews for the book with the given ISBN, ordered by rating from highest to lowest,
     * and converts them to {@link ReviewResponseDTO} objects.
     * </p>
     *
     * @param isbn       ISBN of the book for which top reviews are to be retrieved.
     * @param officeName Name of hte {@link Office} where the {@link Book} belongs.
     * @return List of the top 3 {@link ReviewResponseDTO} representing the highest-rated reviews for the book with the
     * specified ISBN.
     */
    @Override
    public List<ReviewResponseDTO> getTopReviewsForDisplayInBookView(String isbn, String officeName) {
        List<Review> reviews = reviewRepository.findTop3ByBookIsbnAndOfficeName(isbn, officeName);
        return reviews.stream()
                .map(reviewConverter::toReviewResponseDTO)
                .toList();
    }
}