package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewQueryService reviewQueryService;
    private final ReviewManagementService reviewManagementService;

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsForBook(@RequestParam @NotBlank String officeName,
                                                                     @RequestParam @NotBlank String isbn) {
        List<ReviewResponseDTO> result =
                reviewQueryService.getAllReviewsByBookIsbnAndByOfficeName(isbn, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable @NotBlank String reviewId) {
        ReviewResponseDTO result = reviewQueryService.getReviewById(UUID.fromString(reviewId));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/top-reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getTopReviewsForBook(@RequestParam @NotBlank String officeName,
                                                                        @RequestParam @NotBlank String isbn) {
        List<ReviewResponseDTO> result = reviewQueryService.getTopReviewsForBook(isbn, officeName);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/insert")
    public ResponseEntity<ReviewResponseDTO> insertNewReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO result = reviewManagementService.insertReview(reviewRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/update")
    public ResponseEntity<ReviewResponseDTO> updateReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO result = reviewManagementService.updateReview(reviewRequestDTO);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId) {
        UUID result = reviewManagementService.deleteReviewById(UUID.fromString(reviewId));

        return ResponseEntity.ok("Successfully deleted review with id " + result);
    }
}
