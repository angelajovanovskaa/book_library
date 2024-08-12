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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book-checkouts")
public class BookCheckoutController {
    private final BookCheckoutQueryService bookCheckoutQueryService;
    private final BookCheckoutManagementService bookCheckoutManagementService;

    @GetMapping("/getAll")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAll(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllPaginated")
    ResponseEntity<Page<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllPaginated(
            @RequestParam @NotBlank String officeName, @RequestParam(defaultValue = "0") int numberOfPages,
            @RequestParam(defaultValue = "5") int pageSize) {
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(numberOfPages, pageSize, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllActive")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllActive(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllActiveBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllPast")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllPast(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllPastBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllNearReturnDate")
    ResponseEntity<List<BookCheckoutReturnReminderResponseDTO>> getAllNearReturnDate(
            @RequestParam @NotBlank String officeName) {
        List<BookCheckoutReturnReminderResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllByTitleContaining")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllByTitleContaining(
            @RequestParam @NotBlank String officeName, @RequestParam String titleSearchTerm) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(officeName, titleSearchTerm);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllBooksForUser")
    ResponseEntity<List<BookCheckoutResponseDTO>> getAllBooksForUser(@RequestParam @NotNull UUID userId) {
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllBooksForUserByTitleContaining")
    ResponseEntity<List<BookCheckoutResponseDTO>> getAllBooksForUserByTitleContaining(
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
