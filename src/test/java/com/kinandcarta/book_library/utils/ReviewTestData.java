package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Review;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReviewTestData {
    public static final UUID REVIEW_ID = UUID.fromString("6a3e9932-4802-4815-8de8-2f1e99bdf841");
    public static final String REVIEW_MESSAGE = "message";
    public static final int REVIEW_RATING = 1;
    public static final double REVIEW_RATING_DOUBLE = 1.0;
    public static final double REVIEW_RATING_ZERO = 0.0;

    public static List<Review> getReviews() {
        Review review1 = new Review(
                REVIEW_ID,
                SharedServiceTestData.DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING,
                BookTestData.getBook(),
                UserTestData.getUser()
        );
        Review review2 = new Review(
                UUID.fromString("6a3e9932-4802-4815-8de8-2f1e99bdf842"),
                SharedServiceTestData.DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING,
                BookTestData.getBook(),
                UserTestData.getUser()
        );

        return List.of(review1, review2);
    }

    public static Review getReview() {
        return getReviews().getFirst();
    }

    public static List<ReviewResponseDTO> getReviewResponseDTOs() {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                BookTestData.BOOK_ISBN,
                UserTestData.USER_EMAIL,
                SharedServiceTestData.DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING
        );
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                BookTestData.BOOK_ISBN,
                UserTestData.USER_EMAIL,
                SharedServiceTestData.DATE_NOW,
                REVIEW_MESSAGE,
                REVIEW_RATING
        );

        return List.of(review1, review2);
    }

    public static ReviewResponseDTO getReviewResponseDTO() {
        return getReviewResponseDTOs().getFirst();
    }

    public static ReviewRequestDTO getReviewRequestDTO() {
        return new ReviewRequestDTO(
                BookTestData.BOOK_ISBN,
                UserTestData.USER_EMAIL,
                REVIEW_MESSAGE,
                REVIEW_RATING
        );
    }
}
