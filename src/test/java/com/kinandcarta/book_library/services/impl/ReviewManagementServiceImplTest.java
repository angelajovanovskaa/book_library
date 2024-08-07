package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.BookAverageRatingCalculator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.kinandcarta.book_library.utils.BookTestData.getBook;
import static com.kinandcarta.book_library.utils.OfficeTestData.OFFICE;
import static com.kinandcarta.book_library.utils.ReviewTestData.*;
import static com.kinandcarta.book_library.utils.UserTestData.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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
        Review review = getReview();
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();
        ReviewResponseDTO reviewResponseDTO = getReviewResponseDTO();
        Book book = getBook();
        User user = getUser();

        given(reviewConverter.toReview(any())).willReturn(review);
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(bookRepository.findByIsbnAndOffice_Name(any(), any())).willReturn(Optional.of(book));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(List.of(review));
        given(bookAverageRatingCalculator.getAverageRatingOnBook(any())).willReturn(1.0);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTO);

        // when
        ReviewResponseDTO actualResult = reviewManagementService.insertReview(reviewRequestDTO);

        // then
        verify(reviewConverter).toReview(any());
        verify(bookRepository).findByIsbnAndOffice_Name(any(), any());
        verify(userRepository).findByEmail(any());
        verify(reviewRepository).save(any());
        verify(reviewRepository).findAllByBookIsbnAndOfficeName(any(), any());
        verify(bookAverageRatingCalculator).getAverageRatingOnBook(any());
        verify(reviewConverter).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewResponseDTO);
    }

    @Test
    @SneakyThrows
    void insertReview_userWithEmailDoesNotExist_throwsException() {
        // given
        Review review = getReview();
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();
        String userEmail = reviewRequestDTO.userEmail();

        given(reviewConverter.toReview(any())).willReturn(review);
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.insertReview(reviewRequestDTO))
                .withMessage("User with email: " + userEmail + " not found");

        verify(reviewRepository, times(0)).save(any());
        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
    }

    @Test
    @SneakyThrows
    void insertReview_bookWithIsbnDoesNotExist_throwsException() {
        // given
        Review review = getReview();
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();
        User user = getUser();
        Book book = review.getBook();
        String isbn = book.getIsbn();
        String officeName = OFFICE.getName();

        given(reviewConverter.toReview(any())).willReturn(review);
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(bookRepository.findByIsbnAndOffice_Name(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.insertReview(reviewRequestDTO))
                .withMessage("Book with ISBN: " + isbn + " in office: " + officeName + " not found");

        verify(reviewRepository, times(0)).save(any());
        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
    }

    @Test
    void updateReview_reviewUpdateValid_returnReviewDTO() {
        // given
        Review review = getReview();
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();
        ReviewResponseDTO reviewResponseDTO = getReviewResponseDTO();

        given(reviewRepository.findByUserEmailAndBookIsbn(any(), any())).willReturn(Optional.of(review));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(List.of(review));
        given(bookAverageRatingCalculator.getAverageRatingOnBook(any())).willReturn(1.0);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTO);

        // when
        ReviewResponseDTO actualResult = reviewManagementService.updateReview(reviewRequestDTO);

        // then
        verify(reviewRepository).findByUserEmailAndBookIsbn(any(), any());
        verify(reviewRepository).save(any());
        verify(reviewRepository).findAllByBookIsbnAndOfficeName(any(), any());
        verify(bookAverageRatingCalculator).getAverageRatingOnBook(any());
        verify(reviewConverter).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewResponseDTO);
    }

    @Test
    void deleteReviewById_reviewDeleteValid_returnUUID() {
        // given
        Review review = getReview();
        UUID reviewId = review.getId();

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(List.of(review));
        given(bookAverageRatingCalculator.getAverageRatingOnBook(any())).willReturn(1.0);

        // when
        UUID actualResult = reviewManagementService.deleteReviewById(reviewId);

        // then
        verify(reviewRepository).findById(any());
        verify(reviewRepository).deleteById(any());
        verify(reviewRepository).findAllByBookIsbnAndOfficeName(any(), any());
        verify(bookAverageRatingCalculator).getAverageRatingOnBook(any());

        assertThat(actualResult).isEqualTo(reviewId);
    }

    @Test
    void deleteReviewById_reviewsListIsEmpty_return0() {
        // given
        Review review = getReview();
        Book book = review.getBook();
        String isbn = book.getIsbn();
        String officeName = OFFICE.getName();
        UUID reviewId = review.getId();

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(List.of());

        // when
        reviewManagementService.deleteReviewById(reviewId);

        // then
        verify(reviewRepository).findById(any());
        verify(reviewRepository).deleteById(any());
        verify(reviewRepository).findAllByBookIsbnAndOfficeName(any(), any());
        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
        verify(bookRepository).updateRatingByIsbnAndOfficeName(0.0, isbn, officeName);
    }

    @Test
    @SneakyThrows
    void deleteReviewById_reviewWithIdNotFound_throwsException() {
        // given
        UUID id = UUID.randomUUID();

        given(reviewRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.deleteReviewById(id))
                .withMessage("Review with id " + id + " not found");

        verify(bookRepository, times(0)).findByIsbn(any());
        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
    }
}