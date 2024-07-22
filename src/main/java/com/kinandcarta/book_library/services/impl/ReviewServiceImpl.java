package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link ReviewService} that contains service
 * logic implementation of the CRUD operations for Review.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CalculateAverageRatingOnBookImpl calculateAverageReviewRatingOnBook;

    /**
     * Retrieves all reviews in our system.
     * <hr>
     *
     * @return List of {@link ReviewDTO}
     */
    public List<ReviewDTO> getAllReviews() {

        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * Retrieves ReviewDTO object by its id.
     * <hr>
     *
     * @param id Type: UUID
     * @return ReviewDTO
     */
    public ReviewDTO getReviewById(UUID id) {

        Review review = getReview(id);

        return reviewConverter.toReviewDTO(review);
    }

    /**
     * Retrieves ReviewDTO objects for Book with isbn = param.
     * <hr>
     *
     * @param isbn Type: String
     * @return List of {@link ReviewDTO}
     */
    public List<ReviewDTO> getAllReviewsByBookIsbn(String isbn) {

        Book book = getBook(isbn);

        List<Review> reviews = reviewRepository.findAllByBookIsbn(book.getIsbn());

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * Retrieves the top 3 reviews of a Book by provided ISBN in descending order.
     * <hr>
     *
     * @param isbn Type: String
     * @return List of {@link ReviewDTO}
     */
    @Override
    public List<ReviewDTO> getTopReviewsForDisplayInBookView(String isbn) {

        Book book = getBook(isbn);

        List<Review> reviews = reviewRepository.findTop3ByBookIsbnOrderByRatingDesc(book.getIsbn());

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * Saves new Review object.
     * <hr>
     * Method also updates the ratingFromFirm attribute in the Book object.
     *
     * @param reviewDTO Type: ReviewDTO
     * @return {@link ReviewDTO}
     */
    public ReviewDTO insertReview(ReviewDTO reviewDTO) {

        Review review = reviewConverter.toReview(reviewDTO);

        Book book = getBook(reviewDTO.bookISBN());
        review.addBook(book);

        User user = getUser(reviewDTO.userEmail());
        review.addUser(user);

        reviewRepository.save(review);

        String isbn = book.getIsbn();
        double rating = calculateBookRating(isbn);
        book.setRatingFromFirm(rating);

        bookRepository.save(book);

        return reviewConverter.toReviewDTO(review);
    }

    /**
     * Updates an existing Review in our db using the data from {@link ReviewDTO}.
     * <hr>
     *
     * @param reviewDTO Type: ReviewDTO
     * @return {@link ReviewDTO}
     */
    @Override
    public ReviewDTO updateReview(ReviewDTO reviewDTO) {

        Book book = getBook(reviewDTO.bookISBN());
        String isbn = book.getIsbn();

        User user = getUser(reviewDTO.userEmail());
        String email = user.getEmail();

        Review review = getReview(email, isbn);

        LocalDate newReviewDate = LocalDate.now();
        review.setDate(newReviewDate);

        int newRatingFromUser = reviewDTO.rating();
        review.setRating(newRatingFromUser);

        String newMessageFromUser = reviewDTO.message();
        review.setMessage(newMessageFromUser);

        reviewRepository.save(review);

        double newRatingOnBook = calculateBookRating(isbn);
        book.setRatingFromFirm(newRatingOnBook);
        bookRepository.save(book);

        return reviewConverter.toReviewDTO(review);
    }

    /**
     * Deletes Review by its id.
     * <hr>
     *
     * @param id Type: UUID
     * @return {@link ReviewDTO}
     */
    public ReviewDTO deleteReviewById(UUID id) {

        Review review = getReview(id);

        Book book = review.getBook();

        String isbn = book.getIsbn();

        reviewRepository.deleteById(id);

        double bookRatingFromFirm = calculateBookRating(isbn);

        book.setRatingFromFirm(bookRatingFromFirm);
        bookRepository.save(book);

        return reviewConverter.toReviewDTO(review);
    }

    private double calculateBookRating(String isbn) {

        List<Review> reviews = reviewRepository.findAllByBookIsbn(isbn);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        List<Integer> reviewRatings = reviews.stream().map(Review::getRating).toList();

        return calculateAverageReviewRatingOnBook.getAverageRatingOnBook(reviewRatings);
    }

    private Review getReview(UUID id) {

        Optional<Review> optionalReview = reviewRepository.findById(id);

        if (optionalReview.isEmpty()) {
            throw new ReviewNotFoundException(id);
        }

        return optionalReview.get();
    }

    private Review getReview(String email, String isbn) {

        Optional<Review> optionalReview = reviewRepository.findByUserEmailAndBookIsbn(email, isbn);

        if (optionalReview.isEmpty()) {
            throw new ReviewNotFoundException(email, isbn);
        }

        return optionalReview.get();
    }

    private Book getBook(String isbn) {

        Optional<Book> optionalBook = this.bookRepository.findByIsbn(isbn);

        if (optionalBook.isEmpty()) {
            throw new BookNotFoundException(isbn);
        }

        return optionalBook.get();
    }

    private User getUser(String email) {

        Optional<User> optionalUser = this.userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(email);
        }

        return optionalUser.get();
    }
}
