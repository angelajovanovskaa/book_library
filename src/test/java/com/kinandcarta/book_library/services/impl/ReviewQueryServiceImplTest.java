package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.ReviewConverter;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.repositories.ReviewRepository;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
        List<ReviewResponseDTO> reviewResponseDTOs = ReviewTestData.getReviewResponseDTOs();

        given(reviewRepository.findAll()).willReturn(ReviewTestData.getReviews());
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTOs.get(0),
                reviewResponseDTOs.get(1));

        // when
        List<ReviewResponseDTO> actualResult = reviewQueryService.getAllReviews();

        // then
        assertThat(actualResult).isEqualTo(reviewResponseDTOs);
    }

    @Test
    void getReviewById_reviewWithGivenIdExists_returnReviewDTO() {
        // given
        given(reviewRepository.findById(any())).willReturn(Optional.of(ReviewTestData.getReview()));
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        ReviewResponseDTO actualResult = reviewQueryService.getReviewById(ReviewTestData.REVIEW_ID);

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewWithGivenIdDoesNotExist_throwsException() {
        // given
        given(reviewRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> reviewQueryService.getReviewById(ReviewTestData.REVIEW_ID))
                .withMessage("Review with id " + ReviewTestData.REVIEW_ID + " not found");
    }

    @Test
    void getAllReviewsByBookIsbnAndByOfficeName_atLeastOneReviewExists_returnListOfReviewDTOs() {
        // given
        List<ReviewResponseDTO> reviewResponseDTOs = ReviewTestData.getReviewResponseDTOs();

        given(reviewRepository.findAllByBookIsbnAndOfficeName(any(), any())).willReturn(ReviewTestData.getReviews());
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTOs.get(0),
                reviewResponseDTOs.get(1));

        // when
        List<ReviewResponseDTO> actualResult =
                reviewQueryService.getAllReviewsByBookIsbnAndByOfficeName(BookTestData.BOOK_ISBN,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(reviewResponseDTOs);
    }

    @Test
    void getTopReviewsForDisplayInBookView_topReviewsExist_returnListOfTopReviewDTOs() {
        // given
        List<ReviewResponseDTO> reviewResponseDTOs = ReviewTestData.getReviewResponseDTOs();

        given(reviewRepository.findTop3ByBookIsbnAndOfficeName(any(), any())).willReturn(ReviewTestData.getReviews());
        given(reviewConverter.toReviewResponseDTO(any())).willReturn(reviewResponseDTOs.get(0),
                reviewResponseDTOs.get(1));

        // when
        List<ReviewResponseDTO> actualResult =
                reviewQueryService.getTopReviewsForBook(BookTestData.BOOK_ISBN,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(reviewResponseDTOs);
    }
}