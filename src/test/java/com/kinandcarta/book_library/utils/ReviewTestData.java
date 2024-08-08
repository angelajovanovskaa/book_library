package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Review;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.BookTestData.getBook;
import static com.kinandcarta.book_library.utils.SharedTestData.DATE_NOW;
import static com.kinandcarta.book_library.utils.UserTestData.getUser;

@UtilityClass
public class ReviewTestData {
    public static final UUID REVIEW_ID = UUID.fromString("6a3e9932-4802-4815-8de8-2f1e99bdf841");
    public static final String REVIEW_MESSAGE = "message";
    public static final int REVIEW_RATING = 1;

    public static List<Review> getReviews() {
        List<Review> reviews = new ArrayList<>();
        Review review1 = new Review(
                REVIEW_ID,
                DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING,
                getBook(),
                getUser()
        );
        reviews.add(review1);
        Review review2 = new Review(
                UUID.fromString("6a3e9932-4802-4815-8de8-2f1e99bdf842"),
                DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING,
                getBook(),
                getUser()
        );
        reviews.add(review2);

        return reviews;
    }

    public static Review getReview() {
        return getReviews().getFirst();
    }

    public static List<ReviewResponseDTO> getReviewResponseDTOs() {
        List<ReviewResponseDTO> reviewResponseDTOs = new ArrayList<>();
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                getBook().getIsbn(),
                getUser().getEmail(),
                DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING

        );
        reviewResponseDTOs.add(review1);
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                getBook().getIsbn(),
                getUser().getEmail(),
                DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING

        );
        reviewResponseDTOs.add(review2);

        return reviewResponseDTOs;
    }

    public static ReviewResponseDTO getReviewResponseDTO() {
        return getReviewResponseDTOs().getFirst();
    }

    public static ReviewRequestDTO getReviewRequestDTO() {
        return new ReviewRequestDTO(
                getBook().getIsbn(),
                getUser().getEmail(),
                REVIEW_MESSAGE,
                REVIEW_RATING
        );
    }
}
