package com.kinandcarta.book_library.controllers;


import com.kinandcarta.book_library.dtos.ReviewRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewManagementController {

    private final ReviewManagementService service;

    @PostMapping("/insert")
    public ResponseEntity<ReviewResponseDTO> insertNewReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO result = service.insertReview(reviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/update")
    public ResponseEntity<ReviewResponseDTO> updateReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO result = service.updateReview(reviewRequestDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId) {
        UUID result = service.deleteReviewById(reviewId);
        return ResponseEntity.ok("Successfully deleted review with id " + result);
    }
}
