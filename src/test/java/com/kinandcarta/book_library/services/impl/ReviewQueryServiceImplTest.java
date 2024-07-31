package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewQueryServiceImplTest {
    private static final Office OFFICE = new Office("Skopje kancelarija");

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewConverter reviewConverter;

    @InjectMocks
    private ReviewQueryServiceImpl reviewQueryService;

    @Test
    void getAllReviews_atLeastOneReviewExists_returnListOfReviewDTOs() {
        // given
        List<Review> reviews = getReviews();
        List<ReviewResponseDTO> reviewResponseDTOS = getReviewResponseDTOs();

        given(reviewRepository.findAll()).willReturn(reviews);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTOS.get(0),
                reviewResponseDTOS.get(1),
                reviewResponseDTOS.get(2));

        // when
        List<ReviewResponseDTO> actualResult = reviewQueryService.getAllReviews();

        // then
        verify(reviewRepository).findAll();
        verify(reviewConverter, times(3)).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewResponseDTOS);
    }

    @Test
    void getReviewById_reviewWithGivenIdExists_returnReviewDTO() {
        // given
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-100000000000");
        List<Review> reviews = getReviews();
        Review review = reviews.getFirst();
        List<ReviewResponseDTO> reviewResponseDTOS = getReviewResponseDTOs();
        ReviewResponseDTO reviewResponseDTO = reviewResponseDTOS.getFirst();

        given(reviewRepository.findById(id)).willReturn(Optional.of(review));
        given(reviewConverter.toReviewResponseDTO(review)).willReturn(reviewResponseDTO);

        // when
        ReviewResponseDTO actualResult = reviewQueryService.getReviewById(id);

        // then
        verify(reviewRepository).findById(id);
        verify(reviewConverter).toReviewResponseDTO(review);

        assertThat(actualResult).isEqualTo(reviewResponseDTO);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewWithGivenIdDoesNotExists_throwsException() {
        // given
        UUID id = UUID.randomUUID();

        given(reviewRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> reviewQueryService.getReviewById(id))
                .withMessage("Review with id " + id + " not found");

        verify(reviewConverter, times(0)).toReviewResponseDTO(any());
    }

    @Test
    void getAllReviewsByBookIsbnAndByOfficeName_reviewsExist_returnListOfReviewDTOs() {
        // given
        String isbn = "isbn1";
        String officeName = OFFICE.getName();
        List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().getIsbn().equals(isbn)).toList();
        List<ReviewResponseDTO> reviewResponseDTOS =
                getReviewResponseDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(reviewRepository.findAllByBookIsbnAndOfficeName(isbn, officeName)).willReturn(reviews);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTOS.get(0),
                reviewResponseDTOS.get(1));

        // when
        List<ReviewResponseDTO> actualResult =
                reviewQueryService.getAllReviewsByBookIsbnAndByOfficeName(isbn, officeName);

        // then
        verify(reviewRepository).findAllByBookIsbnAndOfficeName(isbn, officeName);
        verify(reviewConverter, times(2)).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewResponseDTOS);
    }

    @Test
    void getTopReviewsForDisplayInBookView_topReviewsExist_returnListOfTopReviewDTOs() {
        // given
        String isbn = "isbn1";
        List<Review> reviews = getReviews();
        List<ReviewResponseDTO> reviewDTOs = getReviewResponseDTOs();

        given(reviewRepository.findTop3ByBookIsbnAndOfficeName(any(), any())).willReturn(reviews);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewDTOs.get(0), reviewDTOs.get(1),
                reviewDTOs.get(2));

        // when
        List<ReviewResponseDTO> actualResult =
                reviewQueryService.getTopReviewsForDisplayInBookView(isbn, OFFICE.getName());

        // then
        verify(reviewRepository).findTop3ByBookIsbnAndOfficeName(any(), any());
        verify(reviewConverter, times(3)).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOs);
    }

    private List<Book> getBooks() {
        Book book1 = new Book(
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
        Book book2 = new Book(
                "isbn2",
                OFFICE,
                "title2",
                "description2",
                "summary2",
                0,
                "MK",
                0.0,
                0.0,
                "image2",
                BookStatus.REQUESTED,
                new String[0],
                new HashSet<>(),
                new ArrayList<>()
        );

        return List.of(book1, book2);
    }

    private Book getBook() {
        return getBooks().getFirst();
    }

    private List<User> getUsers() {
        User user1 = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-010000000000"),
                "fullname1",
                null,
                "email1",
                "USER",
                "password1",
                OFFICE
        );
        User user2 = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-020000000000"),
                "fullname2",
                null,
                "email2",
                "USER",
                "password2",
                OFFICE
        );

        return List.of(user1, user2);
    }

    private User getUser() {
        return getUsers().getFirst();
    }

    private List<Review> getReviews() {
        Review review1 = new Review(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                "message1",
                1,
                getBook(),
                getUser()
        );
        Review review2 = new Review(
                UUID.fromString("123e4567-e89b-12d3-a456-200000000000"),
                LocalDate.now(),
                "message2",
                2,
                getBook(),
                getUser()
        );
        Review review3 = new Review(
                UUID.fromString("123e4567-e89b-12d3-a456-300000000000"),
                LocalDate.now(),
                "message3",
                3,
                getBooks().get(1),
                getUsers().get(1)
        );

        return List.of(review1, review2, review3);
    }

    private List<ReviewResponseDTO> getReviewResponseDTOs() {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                getBook().getIsbn(),
                getUser().getEmail(),
                LocalDate.now(),
                "message1",
                1

        );
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                getBook().getIsbn(),
                getUser().getEmail(),
                LocalDate.now(),
                "message2",
                2

        );
        ReviewResponseDTO review3 = new ReviewResponseDTO(
                getBooks().getLast().getIsbn(),
                getUser().getEmail(),
                LocalDate.now(),
                "message3",
                3

        );

        return List.of(review1, review2, review3);
    }
}