package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.ReviewDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewConverterTest {

    private final ReviewConverter reviewConverter = new ReviewConverter();

    @Test
    void toReviewDTO() {

        final Review review = getReview();
        final ReviewDTO reviewDTO = getReviewDTO();

        final ReviewDTO actualResult = reviewConverter.toReviewDTO(review);

        assertThat(actualResult).isEqualTo(reviewDTO);
    }

    @Test
    void toReview() {

        final Review review = getReview();
        final ReviewDTO reviewDTO = getReviewDTO();

        final Review actualResult = reviewConverter.toReview(reviewDTO);

        assertThat(actualResult).isEqualTo(review);
    }

    private Review getReview() {
        UUID reviewId = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        Book book = getBooks().getFirst();
        User user = getUsers().getFirst();

        return new Review(
                reviewId,
                LocalDate.now(),
                "message1",
                1,
                book,
                user
        );
    }

    private ReviewDTO getReviewDTO() {

        UUID reviewId = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        Book book = getBooks().getFirst();
        User user = getUsers().getFirst();

        return new ReviewDTO(
                reviewId,
                LocalDate.now(),
                "message1",
                1,
                book.getIsbn(),
                user.getEmail()
        );
    }

    private List<Book> getBooks() {

        String[] genres = {"genre1", "genre2"};

        Book book1 = new Book(
                "isbn1",
                getOffice(),
                "title1",
                "description1",
                "summary1",
                0,
                "MK",
                0.0,
                0.0,
                "image1",
                BookStatus.REQUESTED,
                genres,
                new HashSet<>(),
                new ArrayList<>()
        );

        Book book2 = new Book(
                "isbn2",
                getOffice(),
                "title2",
                "description2",
                "summary2",
                0,
                "MK",
                0.0,
                0.0,
                "image2",
                BookStatus.REQUESTED,
                genres,
                new HashSet<>(),
                new ArrayList<>()
        );

        return List.of(book1, book2);
    }

    private List<User> getUsers() {

        UUID id1 = UUID.fromString("123e4567-e89b-12d3-a456-010000000000");
        UUID id2 = UUID.fromString("123e4567-e89b-12d3-a456-020000000000");

        User user1 = new User(
                id1,
                "fullname1",
                null,
                "email1",
                "USER",
                "password1",
                getOffice()
        );

        User user2 = new User(
                id2,
                "fullname2",
                null,
                "email2",
                "USER",
                "password2",
                getOffice()
        );

        return List.of(user1, user2);
    }

    private Office getOffice() {

        return new Office("Skopje kancelarija");
    }
}
