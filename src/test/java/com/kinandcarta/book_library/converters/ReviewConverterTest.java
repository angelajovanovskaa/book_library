package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewConverterTest {

    private final ReviewConverter reviewConverter = new ReviewConverter();

    @Test
    void toReviewResponseDTO_convertsReviewToReviewResponseDTOActionIsValid_returnsReviewResponseDTO() {
        // given

        // when
        ReviewResponseDTO actualResult = reviewConverter.toReviewResponseDTO(ReviewTestData.getReview());

        // then
        assertThat(actualResult.bookISBN()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(actualResult.userEmail()).isEqualTo(UserTestData.USER_EMAIL);
        assertThat(actualResult.date()).isEqualTo(SharedTestData.DATE_NOW);
        assertThat(actualResult.message()).isEqualTo(ReviewTestData.REVIEW_MESSAGE);
        assertThat(actualResult.rating()).isEqualTo(ReviewTestData.REVIEW_RATING);
    }

    @Test
    void toReview_convertsReviewRequestDTOToReviewActionIsValid_returnsReview() {
        // given

        // when
        Review actualResult = reviewConverter.toReview(ReviewTestData.getReviewRequestDTO());

        // then
        assertThat(actualResult.getMessage()).isEqualTo(ReviewTestData.REVIEW_MESSAGE);
        assertThat(actualResult.getRating()).isEqualTo(ReviewTestData.REVIEW_RATING);
        assertThat(actualResult).hasNoNullFieldsOrPropertiesExcept("id", "date", "book", "user");
    }
}