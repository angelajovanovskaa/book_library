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
        Book book = new Book(
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
        );
        User user = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-010000000000"),
                "fullname1",
                null,
                "email1",
                "USER",
                "password1",
                OFFICE
        );
        final Review review = new Review(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                "message1",
                1,
                book,
                user
        );

        // when
        final ReviewResponseDTO actualResult = reviewConverter.toReviewResponseDTO(review);

        // then
        assertThat(actualResult.bookISBN()).isEqualTo("isbn1");
        assertThat(actualResult.userEmail()).isEqualTo("email1");
        assertThat(actualResult.date()).isEqualTo(LocalDate.now());
        assertThat(actualResult.message()).isEqualTo("message1");
        assertThat(actualResult.rating()).isEqualTo(1);
    }

    @Test
    void toReview_convertsReviewRequestDTOToReviewActionIsValid_returnsReview() {
        // given
        final ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(
                "Skopje",
                "isbn1",
                "user1",
                "message1",
                1
        );

        // when
        final Review actualResult = reviewConverter.toReview(reviewRequestDTO);

        // then
        assertThat(actualResult.getId()).isNull();
        assertThat(actualResult.getDate()).isNull();
        assertThat(actualResult.getMessage()).isEqualTo("message1");
        assertThat(actualResult.getRating()).isEqualTo(1);
        assertThat(actualResult.getBook()).isNull();
        assertThat(actualResult.getUser()).isNull();
    }
}