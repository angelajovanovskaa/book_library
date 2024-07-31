package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.services.BookCheckoutManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookCheckoutManagement")
public class BookCheckoutManagementController {
    private final BookCheckoutManagementService bookCheckoutManagementService;

    @PostMapping("/borrow")
    public ResponseEntity<BookCheckoutResponseDTO> borrowBookItem(
            @Valid @RequestBody BookCheckoutRequestDTO bookCheckoutRequestDTO) {
        BookCheckoutResponseDTO response = bookCheckoutManagementService.borrowBookItem(bookCheckoutRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/return")
    public ResponseEntity<BookCheckoutResponseDTO> returnBookItem(
            @Valid @RequestBody BookCheckoutRequestDTO bookCheckoutRequestDTO) {
        BookCheckoutResponseDTO response = bookCheckoutManagementService.returnBookItem(bookCheckoutRequestDTO);

        return ResponseEntity.ok(response);
    }
}
