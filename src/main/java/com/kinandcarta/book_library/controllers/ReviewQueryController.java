package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewQueryService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewQueryController {

    private final ReviewQueryService service;

    @GetMapping("")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviewsForBook(@RequestParam @NotBlank String officeName,
                                                                        @RequestParam @NotBlank String bookISBN) {
        List<ReviewResponseDTO> result = service.getAllReviewsByBookIsbnAndByOfficeName(bookISBN, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable @NotBlank String reviewId) {
        ReviewResponseDTO result = service.getReviewById(UUID.fromString(reviewId));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/top-reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getTopReviewsForBookView(@RequestParam @NotBlank String officeName,
                                                                            @RequestParam @NotBlank String bookISBN) {
        List<ReviewResponseDTO> result = service.getTopReviewsForDisplayInBookView(bookISBN, officeName);

        return ResponseEntity.ok(result);
    }
}
