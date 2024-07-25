package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewConverterTest {
    private final static Office OFFICE = new Office("Skopje kancelarija");

    private final ReviewConverter reviewConverter = new ReviewConverter();

    @Test
    void toReviewResponseDTO_convertsReviewToReviewResponseDTOActionIsValid_returnsReviewResponseDTO() {
        // given
        final Review review = getReview();
        final Book book = review.getBook();
        final User user = review.getUser();
        final ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO(
                book.getIsbn(),
                user.getEmail(),
                LocalDate.now(),
                review.getMessage(),
                review.getRating()
        );

        // when
        final ReviewResponseDTO actualResult = reviewConverter.toReviewResponseDTO(review);

        // then
        assertThat(actualResult).isEqualTo(reviewResponseDTO);
    }

    @Test
    void toReview_convertsReviewRequestDTOToReviewActionIsValid_returnsReview() {
        // given
        final Review review = getReview();
        final Book book = review.getBook();
        final User user = review.getUser();
        final ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(
                book.getIsbn(),
                user.getEmail(),
                review.getMessage(),
                review.getRating()
        );

        // when
        final Review actualResult = reviewConverter.toReview(reviewRequestDTO);
        actualResult.setId(UUID.fromString("123e4567-e89b-12d3-a456-100000000000"));
        actualResult.setDate(LocalDate.now());
        actualResult.setBook(book);
        actualResult.setUser(user);

        // then
        assertThat(actualResult).isEqualTo(review);
    }

    private Review getReview() {
        return new Review(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                "message1",
                1,
                new Book(
                        "isbn1",
                        OFFICE,
                        "title1",
                        "description1",
                        "summary1",
                        0,
                        "MK",
                        0.0,
                        0.0,
                        "image1",
                        BookStatus.REQUESTED,
                        new String[0],
                        new HashSet<>(),
                        new ArrayList<>()
                ),
                new User(
                        UUID.fromString("123e4567-e89b-12d3-a456-010000000000"),
                        "fullname1",
                        null,
                        "email1",
                        "USER",
                        "password1",
                        OFFICE
                )
        );
    }
}