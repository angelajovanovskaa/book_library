package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        List<ReviewResponseDTO> response =
                reviewQueryService.getAllReviewsByBookIsbnAndByOfficeName(isbn, officeName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable @NotNull UUID reviewId) {
        ReviewResponseDTO response = reviewQueryService.getReviewById(reviewId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getTopReviewsForBook(@RequestParam @NotBlank String officeName,
                                                                        @RequestParam @NotBlank String isbn) {
        List<ReviewResponseDTO> response = reviewQueryService.getTopReviewsForBook(isbn, officeName);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/insert")
    public ResponseEntity<ReviewResponseDTO> insertReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO response = reviewManagementService.insertReview(reviewRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ReviewResponseDTO> updateReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO response = reviewManagementService.updateReview(reviewRequestDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable @NotNull UUID reviewId) {
        UUID response = reviewManagementService.deleteReviewById(reviewId);

        return ResponseEntity.ok("Successfully deleted review with id " + response);
    }
}
