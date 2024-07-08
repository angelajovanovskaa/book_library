package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 *  <h4><i>This class is used for implementing calculations used by method in the service logic
 *  by the model Review.</i></h4>
 *  <h6><i>Methods:</i></h6>
 *  <un>
 *      <li>getAverageRatingOnBook - calculates the average rating given by employees who borrowed the book.</li>
 *  </un>
 */
@RequiredArgsConstructor
public class ReviewCalculations {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public Double getAverageRatingOnBook(String isbn) {
        Optional<Book> book = this.bookRepository.findById(isbn);

        if (book.isEmpty()){
            throw new BookNotFoundException(isbn);
        }

        List<Review> reviews = this.reviewRepository.findAllByBook(book.get());

        Double rating = 0.0;

        if (reviews.isEmpty()){
            return rating;
        }

        for (Review review : reviews){
            rating += review.getRating();
        }

        return rating / reviews.size();
    }
}
