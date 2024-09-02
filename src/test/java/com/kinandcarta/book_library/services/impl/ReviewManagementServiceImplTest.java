package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.DuplicateUserReviewException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.BookAverageRatingCalculator;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewManagementServiceImplTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewConverter reviewConverter;

    @Mock
    private BookAverageRatingCalculator bookAverageRatingCalculator;

    @InjectMocks
    private ReviewManagementServiceImpl reviewManagementService;

    @Test
    void insertReview_reviewInsertIsValid_returnReviewDTO() {
        // given
        given(reviewConverter.toReview(any())).willReturn(ReviewTestData.getReview());
        given(userRepository.findByEmail(any())).willReturn(Optional.of(UserTestData.getUser()));
        given(bookRepository.findByIsbnAndOfficeName(any(), any())).willReturn(Optional.of(BookTestData.getBook()));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(
                List.of(ReviewTestData.getReview()));
        given(bookAverageRatingCalculator.getAverageRatingOnBook(any())).willReturn(
                ReviewTestData.REVIEW_RATING_DOUBLE);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        ReviewResponseDTO actualResult = reviewManagementService.insertReview(ReviewTestData.getReviewRequestDTO());

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void insertReview_userWithEmailDoesNotExist_throwsException() {
        // given
        given(reviewConverter.toReview(any())).willReturn(ReviewTestData.getReview());
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.insertReview(ReviewTestData.getReviewRequestDTO()))
                .withMessage("User with email: " + UserTestData.USER_EMAIL + " not found");
    }

    @Test
    @SneakyThrows
    void insertReview_bookWithIsbnDoesNotExist_throwsException() {
        // given
        given(reviewConverter.toReview(any())).willReturn(ReviewTestData.getReview());
        given(userRepository.findByEmail(any())).willReturn(Optional.of(UserTestData.getUser()));
        given(bookRepository.findByIsbnAndOfficeName(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.insertReview(ReviewTestData.getReviewRequestDTO()))
                .withMessage("Book with ISBN: " + BookTestData.BOOK_ISBN + " in office: " +
                        SharedServiceTestData.SKOPJE_OFFICE_NAME + " not found");
    }

    @Test
    @SneakyThrows
    void insertReview_userAlreadyReviewedBook_throwsException(){
        //given
        given(reviewRepository.findByUserEmailAndBookIsbn(UserTestData.USER_EMAIL, BookTestData.BOOK_ISBN)).willReturn(
                Optional.of(ReviewTestData.getReview()));

        //when & then
        assertThatExceptionOfType(DuplicateUserReviewException.class)
                .isThrownBy(() -> reviewManagementService.insertReview(ReviewTestData.getReviewRequestDTO()))
                .withMessage("The user has already left a review for this book.");
    }

    @Test
    void updateReview_reviewUpdateValid_returnReviewDTO() {
        // given
        given(reviewRepository.findByUserEmailAndBookIsbn(any(), any())).willReturn(
                Optional.of(ReviewTestData.getReview()));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(
                List.of(ReviewTestData.getReview()));
        given(bookAverageRatingCalculator.getAverageRatingOnBook(any())).willReturn(
                ReviewTestData.REVIEW_RATING_DOUBLE);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        ReviewResponseDTO actualResult = reviewManagementService.updateReview(ReviewTestData.getReviewRequestDTO());

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void updateReview_reviewWithUserEmailAndBookISBNDoesNotExist_throwsException() {
        // given
        given(reviewRepository.findByUserEmailAndBookIsbn(any(), any())).willReturn(
                Optional.empty());

        // when & then
        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.updateReview(ReviewTestData.getReviewRequestDTO()))
                .withMessage("Review for user: " + UserTestData.USER_EMAIL + " and book with ISBN: " +
                        BookTestData.BOOK_ISBN + " not found");
    }

    @Test
    void deleteReviewById_reviewDeleteValid_returnUUID() {
        // given
        given(reviewRepository.findById(any())).willReturn(Optional.of(ReviewTestData.getReview()));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(
                List.of(ReviewTestData.getReview()));
        given(bookAverageRatingCalculator.getAverageRatingOnBook(any())).willReturn(
                ReviewTestData.REVIEW_RATING_DOUBLE);

        // when
        UUID actualResult = reviewManagementService.deleteReviewById(ReviewTestData.REVIEW_ID);

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.REVIEW_ID);
    }

    @Test
    void deleteReviewById_reviewsListIsEmpty_returnReviewUUID() {
        // given
        given(reviewRepository.findById(any())).willReturn(Optional.of(ReviewTestData.getReview()));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(List.of());

        // when
        reviewManagementService.deleteReviewById(ReviewTestData.REVIEW_ID);

        // then
        verify(bookRepository).updateRatingByIsbnAndOfficeName(ReviewTestData.REVIEW_RATING_ZERO,
                BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);
    }

    @Test
    @SneakyThrows
    void deleteReviewById_reviewWithIdDoesNotExist_throwsException() {
        // given
        given(reviewRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.deleteReviewById(ReviewTestData.REVIEW_ID))
                .withMessage("Review with id " + ReviewTestData.REVIEW_ID + " not found");

        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
    }
}