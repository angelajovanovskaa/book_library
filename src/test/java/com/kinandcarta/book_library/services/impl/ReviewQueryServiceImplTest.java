package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Review;
import com.kinandcarta.book_library.repositories.ReviewRepository;
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
import static com.kinandcarta.book_library.utils.ReviewTestData.getReviewResponseDTOs;
import static com.kinandcarta.book_library.utils.ReviewTestData.getReviews;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewQueryServiceImplTest {
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
                reviewResponseDTOS.get(1));

        // when
        List<ReviewResponseDTO> actualResult = reviewQueryService.getAllReviews();

        // then
        verify(reviewRepository).findAll();
        verify(reviewConverter, times(2)).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewResponseDTOS);
    }

    @Test
    void getReviewById_reviewWithGivenIdExists_returnReviewDTO() {
        // given
        List<Review> reviews = getReviews();
        Review review = reviews.getFirst();
        UUID id = review.getId();
        List<ReviewResponseDTO> reviewResponseDTOS = getReviewResponseDTOs();
        ReviewResponseDTO reviewResponseDTO = reviewResponseDTOS.getFirst();

        given(reviewRepository.findById(any())).willReturn(Optional.of(review));
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTO);

        // when
        ReviewResponseDTO actualResult = reviewQueryService.getReviewById(id);

        // then
        verify(reviewRepository).findById(any());
        verify(reviewConverter).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewResponseDTO);
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewWithGivenIdDoesNotExists_throwsException() {
        // given
        UUID id = UUID.randomUUID();

        given(reviewRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> reviewQueryService.getReviewById(id))
                .withMessage("Review with id " + id + " not found");

        verify(reviewConverter, times(0)).toReviewResponseDTO(any());
    }

    @Test
    void getAllReviewsByBookIsbnAndByOfficeName_reviewsExist_returnListOfReviewDTOs() {
        // given
        Book book = getBook();
        String isbn = book.getIsbn();
        String officeName = OFFICE.getName();
        List<Review> reviews = getReviews().stream().filter(obj -> obj.getBook().getIsbn().equals(isbn)).toList();
        List<ReviewResponseDTO> reviewResponseDTOS =
                getReviewResponseDTOs().stream().filter(obj -> obj.bookISBN().equals(isbn)).toList();

        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(reviews);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTOS.get(0),
                reviewResponseDTOS.get(1));

        // when
        List<ReviewResponseDTO> actualResult =
                reviewQueryService.getAllReviewsByBookIsbnAndByOfficeName(isbn, officeName);

        // then
        verify(reviewRepository).findAllByBookIsbnAndOfficeName(any(), any());
        verify(reviewConverter, times(2)).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewResponseDTOS);
    }

    @Test
    void getTopReviewsForDisplayInBookView_topReviewsExist_returnListOfTopReviewDTOs() {
        // given
        Book book = getBook();
        String isbn = book.getIsbn();
        List<Review> reviews = getReviews();
        List<ReviewResponseDTO> reviewDTOs = getReviewResponseDTOs();

        given(reviewRepository.findTop3ByBookIsbnAndOfficeName(any(), any())).willReturn(reviews);
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewDTOs.get(0), reviewDTOs.get(1));

        // when
        List<ReviewResponseDTO> actualResult =
                reviewQueryService.getTopReviewsForDisplayInBookView(isbn, OFFICE.getName());

        // then
        verify(reviewRepository).findTop3ByBookIsbnAndOfficeName(any(), any());
        verify(reviewConverter, times(2)).toReviewResponseDTO(any());

        assertThat(actualResult).isEqualTo(reviewDTOs);
    }
}