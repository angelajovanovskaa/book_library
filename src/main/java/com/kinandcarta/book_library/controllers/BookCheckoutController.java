package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.BookCheckoutRequestDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.services.BookCheckoutManagementService;
import com.kinandcarta.book_library.services.BookCheckoutQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book-checkouts")
@Validated
public class BookCheckoutController {
    private final BookCheckoutQueryService bookCheckoutQueryService;
    private final BookCheckoutManagementService bookCheckoutManagementService;

    @GetMapping
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getBookCheckouts(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/paginated")
    ResponseEntity<Page<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getBookCheckoutsPaginated(
            @RequestParam @NotBlank String officeName, @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) {
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(pageNumber, pageSize, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/active")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getActiveBookCheckouts(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllActiveBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/past")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getPastBookCheckouts(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllPastBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/near-return-date")
    ResponseEntity<List<BookCheckoutReturnReminderResponseDTO>> getBookCheckoutsNearReturnDate(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutReturnReminderResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-title")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getBookCheckoutsByTitleContaining(
            @RequestParam @NotBlank String officeName, @RequestParam String titleSearchTerm) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(officeName, titleSearchTerm);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-user")
    ResponseEntity<List<BookCheckoutResponseDTO>> getBookCheckoutsByUser(@RequestParam @NotNull UUID userId) {
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-user-and-title")
    ResponseEntity<List<BookCheckoutResponseDTO>> getBookCheckoutsByUserAndTitleContaining(
            @RequestParam @NotNull UUID userId, @RequestParam String titleSearchTerm) {
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(userId,
                titleSearchTerm);

        return ResponseEntity.ok(result);
    }

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
