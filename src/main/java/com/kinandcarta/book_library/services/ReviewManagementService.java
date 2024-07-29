package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;

import java.util.UUID;

public interface ReviewManagementService {
    ReviewResponseDTO insertReview(ReviewRequestDTO reviewInsertDTO);

    ReviewResponseDTO updateReview(ReviewRequestDTO reviewRequestDTO);

    UUID deleteReviewById(UUID id);
}