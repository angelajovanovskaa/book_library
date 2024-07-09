package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.DTOs.ReviewDTO;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewConverter {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReviewDTO toReviewDTO(Review review) {

        UUID id = review.getId();
        Date date = review.getDate();
        String message = review.getMessage();
        Integer rating = review.getRating();
        String bookISBN = review.getBook().getISBN();

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

        Optional<Book> book = bookRepository.findById(reviewDTO.bookISBN());

        if (book.isEmpty()) {
            throw new BookNotFoundException(reviewDTO.bookISBN());
        }

        review.setBook(book.get());

        User user = userRepository.findByEmail(reviewDTO.userEmail());
        review.setUser(user);

        return review;
    }
}
