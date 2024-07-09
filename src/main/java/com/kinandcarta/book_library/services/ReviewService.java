package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.DTOs.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    List<ReviewDTO> getAllReviews();

    ReviewDTO getReviewById(UUID id);

    List<ReviewDTO> getAllReviewsByBookId(String isbn);

    List<ReviewDTO> getAllReviewsByUserId(UUID userId);

    ReviewDTO save(ReviewDTO reviewDTO);

    ReviewDTO delete(UUID id);

    Double getAverageRatingOnBook(String bookISBN);
}