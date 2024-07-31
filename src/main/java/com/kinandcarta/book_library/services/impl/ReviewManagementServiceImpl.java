package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.BookAverageRatingCalculator;
import com.kinandcarta.book_library.services.ReviewManagementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * This service provides methods for managing {@link Review} entities.
 * <p>
 * This service provides methods to insert, update, and delete {@link Review} records. It also updates the rating of the
 * associated
 * book after inserting or updating a review.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class ReviewManagementServiceImpl implements ReviewManagementService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewConverter reviewConverter;
    private final BookAverageRatingCalculator bookAverageRatingCalculator;

    /**
     * Inserts a new review into the system and updates the book's rating.
     * <p>
     * This method converts the {@link ReviewRequestDTO} to a {@link Review} entity, associates it with the specified
     * book
     * and user, and saves it to the repository. The book's rating is then updated based on the new review.
     * </p>
     *
     * @param reviewRequestDTO {@link ReviewRequestDTO} containing the review details
     * @return {@link ReviewResponseDTO} representing the saved review
     */
    @Override
    @Transactional
    public ReviewResponseDTO insertReview(ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewConverter.toReview(reviewRequestDTO);

        LocalDate date = LocalDate.now();
        review.setDate(date);
        String isbn = reviewRequestDTO.bookISBN();
        String officeName = reviewRequestDTO.officeName();
        Book book = getBook(isbn, officeName);
        review.addBook(book);
        String email = reviewRequestDTO.userEmail();
        User user = getUser(email);
        review.addUser(user);
        reviewRepository.save(review);


        double ratingFromFirm = calculateBookRating(isbn, officeName);
        bookRepository.updateRatingByIsbnAndOfficeName(isbn, officeName, ratingFromFirm);

        return reviewConverter.toReviewResponseDTO(review);
    }

    /**
     * Updates an existing review in the system.
     * <p>
     * This method updates an existing review with the data provided in the {@link ReviewRequestDTO}. It also updates
     * the
     * book's rating based on the new review details.
     * </p>
     *
     * @param reviewRequestDTO {@link ReviewRequestDTO} containing the updated review details
     * @return {@link ReviewResponseDTO} representing the saved review
     * @throws ReviewNotFoundException if the review to be updated is not found
     */
    @Override
    @Transactional
    public ReviewResponseDTO updateReview(ReviewRequestDTO reviewRequestDTO) {
        String email = reviewRequestDTO.userEmail();
        String isbn = reviewRequestDTO.bookISBN();
        String officeName = reviewRequestDTO.officeName();
        Review review = reviewRepository.findByUserEmailAndBookIsbn(email, isbn)
                .orElseThrow(() -> new ReviewNotFoundException(email, isbn));

        LocalDate newReviewDate = LocalDate.now();
        review.setDate(newReviewDate);
        String newMessageFromUser = reviewRequestDTO.message();
        review.setMessage(newMessageFromUser);
        int newRatingFromUser = reviewRequestDTO.rating();
        review.setRating(newRatingFromUser);
        reviewRepository.save(review);

        double ratingFromFirm = calculateBookRating(isbn, officeName);
        bookRepository.updateRatingByIsbnAndOfficeName(isbn, officeName, ratingFromFirm);

        return reviewConverter.toReviewResponseDTO(review);
    }

    /**
     * Deletes a review by its unique identifier.
     * <p>
     * This method deletes the review with the specified ID and updates the book's rating.
     * </p>
     *
     * @param reviewId UUID of the review to delete
     * @return UUID of the deleted review
     * @throws ReviewNotFoundException if the review with the given ID is not found
     */
    @Override
    @Transactional
    public UUID deleteReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        reviewRepository.deleteById(reviewId);

        Book book = review.getBook();
        String isbn = book.getIsbn();
        Office office = book.getOffice();
        String officeName = office.getName();
        double ratingFromFirm = calculateBookRating(isbn, officeName);
        bookRepository.updateRatingByIsbnAndOfficeName(isbn, officeName, ratingFromFirm);

        return reviewId;
    }

    /**
     * Calculates the rating of the book based on its reviews.
     * <p>
     * This method calculates the average rating of all reviews for the specified book and updates the book's rating.
     * If there are no reviews, the rating is set to 0.0.
     * </p>
     *
     * @param isbn       ISBN of the {@link Book} whose rating needs to be updated.
     * @param officeName Name of hte {@link Office} where the {@link Book} belongs.
     */
    private double calculateBookRating(String isbn, String officeName) {
        List<Review> reviews = reviewRepository.findAllByBookIsbnAndOfficeName(isbn, officeName);

        if (reviews.isEmpty()) {
            return 0.0;
        }
        List<Integer> reviewRatings = reviews.stream().map(Review::getRating).toList();

        return bookAverageRatingCalculator.getAverageRatingOnBook(reviewRatings);
    }

    private Book getBook(String isbn, String officeName) {
        return bookRepository.findByIsbnAndOffice_Name(isbn, officeName)
                .orElseThrow(() -> new BookNotFoundException(isbn, officeName));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}