package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.exceptions.UserNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.repositories.UserRepository;
import com.kinandcarta.book_library.services.BookAverageRatingCalculator;
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
class ReviewManagementServiceImplTest {
    private static final Office OFFICE = new Office("Skopje kancelarija");

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
        given(bookRepository.findByIsbnAndOffice_Name(any(), any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
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
    void insertReview_bookWithIsbnDoesNotExist_throwsException() {
        // given
        Review review = getReview();
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();

        given(reviewConverter.toReview(any())).willReturn(review);
        given(bookRepository.findByIsbnAndOffice_Name(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.insertReview(reviewRequestDTO))
                .withMessage("Book with ISBN: " + reviewRequestDTO.bookISBN() + " in office: " +
                        reviewRequestDTO.officeName() + " not found");

        verify(userRepository, times(0)).findByEmail(any());
        verify(reviewRepository, times(0)).save(any());
        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
    }

    @Test
    @SneakyThrows
    void insertReview_userWithEmailDoesNotExist_throwsException() {
        // given
        Review review = getReview();
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();
        Book book = getBook();

        given(reviewConverter.toReview(any())).willReturn(review);
        given(bookRepository.findByIsbnAndOffice_Name(any(), any())).willReturn(Optional.of(book));
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.insertReview(reviewRequestDTO))
                .withMessage("User with email: " + reviewRequestDTO.userEmail() + " not found");

        verify(reviewRepository, times(0)).save(any());
        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
    }

    @Test
    void updateReview_reviewUpdateValid_returnReviewDTO() {
        // given
        Review review = getReview();
        ReviewRequestDTO reviewRequestDTO = getReviewRequestDTO();
        ReviewResponseDTO reviewResponseDTO = getReviewResponseDTO();
        Book book = getBook();
        User user = getUser();

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
        UUID id = review.getId();
        Book book = review.getBook();

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));
        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(
                Collections.singletonList(review));
        given(bookAverageRatingCalculator.getAverageRatingOnBook(Collections.singletonList(review.getRating())))
                .willReturn(4.0);

        // when
        UUID actualResult = reviewManagementService.deleteReviewById(id);

        // then
        verify(reviewRepository).findById(id);
        verify(reviewRepository).deleteById(id);
        verify(bookRepository).updateRatingByIsbnAndOfficeName(4.0, book.getIsbn(), OFFICE.getName());

        assertThat(actualResult).isEqualTo(id);
    }

    @Test
    @SneakyThrows
    void deleteReviewById_reviewWithIdNotFound_throwsException() {
        // given
        UUID id = UUID.randomUUID();

        given(reviewRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(ReviewNotFoundException.class)
                .isThrownBy(() -> reviewManagementService.deleteReviewById(id))
                .withMessage("Review with id " + id + " not found");

        verify(bookRepository, times(0)).findByIsbn(any());
        verify(bookAverageRatingCalculator, times(0)).getAverageRatingOnBook(any());
    }

    private Book getBook() {
        return new Book(
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
    }

    private User getUser() {
        return new User(
                UUID.fromString("123e4567-e89b-12d3-a456-010000000000"),
                "fullname1",
                null,
                "email1",
                "USER",
                "password1",
                OFFICE
        );
    }

    private Review getReview() {
        return new Review(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                "message1",
                1,
                getBook(),
                getUser()
        );
    }

    private ReviewResponseDTO getReviewResponseDTO() {
        return new ReviewResponseDTO(
                getBook().getIsbn(),
                getUser().getEmail(),
                LocalDate.now(),
                "message1",
                1
        );
    }

    private ReviewRequestDTO getReviewRequestDTO() {
        return new ReviewRequestDTO(
                OFFICE.getName(),
                getBook().getIsbn(),
                getUser().getEmail(),
                "message1",
                1
        );
    }
}