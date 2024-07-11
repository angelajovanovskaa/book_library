package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.dtos.ReviewDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.CalculateAverageRatingOnBook;
import com.kinandcarta.book_library.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kinandcarta.book_library.converters.*;

/**
 *  <h4>Implementation of {@link ReviewService} that contains service
 *  logic implementation of the CRUD operations for Review.</h4>
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CalculateAverageRatingOnBook calculateAverageReviewRatingOnBook;

    /**
     * Using this method, you can get all ReviewDTO objects.
     * <hr>
     *
     * @return (List of ReviewDTO)
     */
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * Using this method, you can get ReviewDTO object by its id.
     * <hr>
     *
     * @param id Type: <i><u>UUID</u></i>
     * @return ReviewDTO
     */
    public ReviewDTO getReviewById(UUID id) {
        Optional<Review> review = reviewRepository.findById(id);

        if (review.isEmpty()){
            throw new ReviewNotFoundException(id);
        }

        return reviewConverter.toReviewDTO(review.get());
    }

    /**
     * Using this method, you can get all ReviewDTO objects for Book with isbn = param.
     * <hr>
     *
     * @param isbn Type: <i><u>String</u></i>
     * @return (List of ReviewDTO)
     */
    public List<ReviewDTO> getAllReviewsByBookId(String isbn) {
        Optional<Book> book = bookRepository.findById(isbn);

        if (book.isEmpty()){
            throw new BookNotFoundException(isbn);
        }

        List<Review> reviews = new ArrayList<>(reviewRepository.findAllByBook(book.get()));

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * Using this method, you can get all ReviewDTO objects by User with userId = param.
     * <hr>
     *
     * @param userId Type: <i><u>UUID</u></i>
     * @return (List of ReviewDTO)
     */
    public List<ReviewDTO> getAllReviewsByUserId(UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()){
            throw new UserNotFoundException(userId);
        }

        List<Review> reviews = new ArrayList<>(reviewRepository.findAllByUser(user.get()));

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * Using this method, you can save new Review.
     * <hr>
     * <a>Method also updates the ratingFromFirm attribute in the Book object.</a>
     * @param reviewDTO Type: (<i><u>ReviewDTO</u></i>)
     * @return (ReviewDTO)
     */
    public ReviewDTO save(ReviewDTO reviewDTO) {

        Review review = reviewConverter.toReview(reviewDTO);

        Book book = review.getBook();

        reviewRepository.save(review);

        book.setRatingFromFirm(calculateBookRating(book));
        bookRepository.save(book);

        return reviewConverter.toReviewDTO(review);
    }

    @Override
    public ReviewDTO update(ReviewDTO reviewDTO) {

        Review review = reviewConverter.toReview(reviewDTO);

        User user = review.getUser();
        Book book = review.getBook();

        Optional<Review> oldVersion = reviewRepository.findByUserAndBook(user, book);
        if (oldVersion.isEmpty()){
            save(reviewDTO);
        }

        oldVersion.ifPresent(reviewRepository::delete);

        reviewRepository.save(review);

        book.setRatingFromFirm(calculateBookRating(book));
        bookRepository.save(book);

        return reviewConverter.toReviewDTO(review);
    }

    /**
     * Using this method, you can delete Review by id.
     * <hr>
     *
     * @param id Type: (<i><u>UUID</u></i>)
     * @return (ReviewDTO)
     */
    public ReviewDTO delete(UUID id) {

        Optional<Review> review = reviewRepository.findById(id);

        if (review.isEmpty()){
            throw new ReviewNotFoundException(id);
        }

        ReviewDTO reviewDTO = reviewConverter.toReviewDTO(review.get());

        Book book = review.get().getBook();

        reviewRepository.delete(review.get());

        book.setRatingFromFirm(calculateBookRating(book));
        bookRepository.save(book);

        return reviewDTO;
    }

    private Double calculateBookRating(Book book){

        List<Review> reviews = reviewRepository.findAllByBook(book);

        if (reviews.isEmpty()){
            return 0.0;
        }

        return calculateAverageReviewRatingOnBook.getAverageRatingOnBook(reviews);
    }
}
