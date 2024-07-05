package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.projections.ReviewDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.ReviewCalculations;
import com.kinandcarta.book_library.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kinandcarta.book_library.converters.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * <b><i>Using this method, you can get all ReviewDTO objects.</i></b>
     * <hr>
     *
     * @return (List of ReviewDTO)
     */
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = this.reviewRepository.findAll();

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

        return this.reviewConverter.toReviewDTO(review.get());
    }

    /**
     * <b><i>Using this method, you can get all ReviewDTO objects for Book with isbn = param.</i></b>
     * <hr>
     *
     * @param isbn Type: <i><u>String</u></i>
     * @return (List of ReviewDTO)
     */
    public List<ReviewDTO> getAllReviewsByBookId(String isbn) {
        Optional<Book> book = this.bookRepository.findById(isbn);

        if (book.isEmpty()){
            throw new BookNotFoundException(isbn);
        }

        List<Review> reviews = new ArrayList<>(this.reviewRepository.findAllByBook(book.get()));

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
        Optional<User> user = this.userRepository.findById(userId);

        if (user.isEmpty()){
            throw new UserNotFoundException(userId);
        }

        List<Review> reviews = new ArrayList<>(this.reviewRepository.findAllByUser(user.get()));

        return reviews.stream().map(reviewConverter::toReviewDTO).toList();
    }

    /**
     * <b><i>Using this method, you can save Review.</i></b>
     * <hr>
     *
     * @param reviewDTO Type: (<i><u>ReviewDTO</u></i>)
     * @return (ReviewDTO)
     */
    public ReviewDTO save(ReviewDTO reviewDTO) {
        Review review = reviewConverter.toReview(reviewDTO);

        this.reviewRepository.save(review);

        return this.reviewConverter.toReviewDTO(review);
    }

    /**
     * <b><i>Using this method, you can delete Review by id.</i></b>
     * <hr>
     *
     * @param id Type: (<i><u>UUID</u></i>)
     * @return (ReviewDTO)
     */
    public ReviewDTO delete(UUID id) {
        Optional<Review> review = this.reviewRepository.findById(id);

        if (review.isEmpty()){
            throw new ReviewNotFoundException(id);
        }

        this.reviewRepository.delete(review.get());

        return this.reviewConverter.toReviewDTO(review.get());
    }

    /**
     * <b><i>Using this method, you can retrieve rating on Book by bookISBN.</i></b>
     * <hr>
     *
     * @param bookISBN Type: (<i><u>String</u></i>)
     * @return (ReviewDTO)
     */
    public Double getAverageRatingOnBook(String bookISBN) {
        ReviewCalculations calculations = new ReviewCalculations(this.bookRepository, this.reviewRepository);

        return calculations.getAverageRatingOnBook(bookISBN);
    }
}
