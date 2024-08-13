package com.kinandcarta.book_library.controllers;


import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewQueryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewQueryController {

    private final ReviewQueryService service;

    @GetMapping("")
    public ResponseEntity<List<ReviewResponseDTO>> findAllReviewsForBook(@RequestParam String officeName,
                                                                         @RequestParam String bookISBN) {
        List<ReviewResponseDTO> result = service.getAllReviewsByBookIsbnAndByOfficeName(bookISBN, officeName);


        return ResponseEntity.ok(result);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> findReviewById(@PathVariable String reviewId) {
        UUID uuid = UUID.fromString(reviewId);
        ReviewResponseDTO result = service.getReviewById(uuid);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/top-reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getTopReviewsForBookView(@RequestParam String officeName,
                                                                            @RequestParam String bookISBN) {
        List<ReviewResponseDTO> result = service.getTopReviewsForDisplayInBookView(bookISBN, officeName);

        return ResponseEntity.ok(result);
    }
}
