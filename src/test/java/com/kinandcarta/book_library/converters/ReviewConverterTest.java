package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Review;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.ReviewTestData.*;
import static com.kinandcarta.book_library.utils.SharedTestData.DATE_NOW;
import static com.kinandcarta.book_library.utils.UserTestData.USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;

class ReviewConverterTest {

    private final ReviewConverter reviewConverter = new ReviewConverter();

    @Test
    void toReviewResponseDTO_convertsReviewToReviewResponseDTOActionIsValid_returnsReviewResponseDTO() {
        // given
        Review review = getReview();

        // when
        ReviewResponseDTO actualResult = reviewConverter.toReviewResponseDTO(review);

        // then
        assertThat(actualResult.bookISBN()).isEqualTo(BOOK_ISBN);
        assertThat(actualResult.userEmail()).isEqualTo(USER_EMAIL);
        assertThat(actualResult.date()).isEqualTo(DATE_NOW);
        assertThat(actualResult.message()).isEqualTo(REVIEW_MESSAGE);
        assertThat(actualResult.rating()).isEqualTo(REVIEW_RATING);
    }

    @Test
    void toReview_convertsReviewRequestDTOToReviewActionIsValid_returnsReview() {
        // given
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();

        // when
        Review actualResult = reviewConverter.toReview(reviewRequestDTO);

        // then
        assertThat(actualResult.getId()).isNull();
        assertThat(actualResult.getDate()).isNull();
        assertThat(actualResult.getMessage()).isEqualTo(REVIEW_MESSAGE);
        assertThat(actualResult.getRating()).isEqualTo(REVIEW_RATING);
        assertThat(actualResult.getBook()).isNull();
        assertThat(actualResult.getUser()).isNull();
    }
}