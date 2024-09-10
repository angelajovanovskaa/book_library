package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import java.util.List;
import java.util.UUID;

public interface ReviewQueryService {
    List<ReviewResponseDTO> getAllReviews();

    ReviewResponseDTO getReviewById(UUID id);

    List<ReviewResponseDTO> getAllReviewsByBookIsbnAndByOfficeName(String isbn, String officeName);

    List<ReviewResponseDTO> getTopReviewsForBook(String isbn, String officeName);
}