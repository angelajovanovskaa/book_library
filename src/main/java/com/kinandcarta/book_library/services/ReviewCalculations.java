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
