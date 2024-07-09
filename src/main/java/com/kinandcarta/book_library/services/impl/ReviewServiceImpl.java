package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.DTOs.ReviewDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.CalculateAverageReviewRatingOnBook;
import com.kinandcarta.book_library.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kinandcarta.book_library.converters.*;

/**
 *  <h4><i> Implementation of {@link ReviewService} that contains service
 *  logic implementation of the CRUD operations for Review.</i></h4>
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CalculateAverageReviewRatingOnBook calculateAverageReviewRatingOnBook;

    /**
     * <b><i>Using this method, you can get all ReviewDTO objects.</i></b>
     * <hr>
     *
     * @return (List of ReviewDTO)
     */
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * <b><i>Using this method, you can get ReviewDTO object by its id.</i></b>
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
     * <b><i>Using this method, you can get all ReviewDTO objects for Book with isbn = param.</i></b>
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
     * <b><i>Using this method, you can get all ReviewDTO objects by User with userId = param.</i></b>
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
     * <b><i>Using this method, you can save new Review.</i></b>
     * <hr>
     * <a>Method also updates the ratingFromFirm attribute in the Book object.</a>
     * @param reviewDTO Type: (<i><u>ReviewDTO</u></i>)
     * @return (ReviewDTO)
     */
    public ReviewDTO save(ReviewDTO reviewDTO) {

        Review review = reviewConverter.toReview(reviewDTO);

        Book book = review.getBook();

        reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findAllByBook(book);
        Double averageRating = calculateAverageReviewRatingOnBook.getAverageRatingOnBook(reviews);

        book.setRatingFromFirm(averageRating);
        bookRepository.save(book);

        return reviewConverter.toReviewDTO(review);
    }

    /**
     * <b><i>Using this method, you can delete Review by id.</i></b>
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

        reviewRepository.delete(review.get());

        return reviewConverter.toReviewDTO(review.get());
    }

    /**
     * <b><i>Using this method, you can retrieve rating on Book by bookISBN.</i></b>
     * <hr>
     *
     * @param bookISBN Type: (<i><u>String</u></i>)
     * @return (ReviewDTO)
     */
    public Double getAverageRatingOnBook(String bookISBN) {

        Optional<Book> book = bookRepository.findById(bookISBN);

        if (book.isEmpty()){
            throw new BookNotFoundException(bookISBN);
        }

        List<Review> reviews = new ArrayList<>(reviewRepository.findAllByBook(book.get()));

        if (reviews.isEmpty()){
            throw new ReviewNotFoundException(bookISBN);
        }

        return calculateAverageReviewRatingOnBook.getAverageRatingOnBook(reviews);
    }
}
