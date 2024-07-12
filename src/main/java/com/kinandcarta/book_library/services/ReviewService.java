package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    List<ReviewDTO> getAllReviews();

    ReviewDTO getReviewById(UUID id);

    List<ReviewDTO> getAllReviewsByBookISBN(String isbn);

    List<ReviewDTO> getAllReviewsByUserId(UUID userId);

    ReviewDTO insertReview(ReviewDTO reviewDTO);

    ReviewDTO updateReview(ReviewDTO reviewDTO);

    ReviewDTO deleteReviewById(UUID id);
}