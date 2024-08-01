package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.BookCheckoutResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutReturnReminderResponseDTO;
import com.kinandcarta.book_library.dtos.BookCheckoutWithUserAndBookItemInfoResponseDTO;
import com.kinandcarta.book_library.services.BookCheckoutQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookCheckoutQuery")
public class BookCheckoutQueryController {
    private final BookCheckoutQueryService bookCheckoutQueryService;

    @GetMapping("/getAll")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAll(@RequestParam String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllPaginated")
    ResponseEntity<Page<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllPaginated(
            @RequestParam String officeName, int numberOfPages, int pageSize) {
        Page<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsPaginated(numberOfPages, pageSize, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllActive")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllActive(@RequestParam String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllActiveBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllPast")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllPast(@RequestParam String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllPastBookCheckouts(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllExpiring")
    ResponseEntity<List<BookCheckoutReturnReminderResponseDTO>> getAllExpiring(
            @RequestParam String officeName) {
        List<BookCheckoutReturnReminderResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsNearingReturnDate(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllByTitleContaining")
    ResponseEntity<List<BookCheckoutWithUserAndBookItemInfoResponseDTO>> getAllByTitleContaining(
            @RequestParam String titleSearchTerm,
            @RequestParam String officeName) {
        List<BookCheckoutWithUserAndBookItemInfoResponseDTO> result =
                bookCheckoutQueryService.getAllBookCheckoutsForBookTitle(officeName, titleSearchTerm);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getForUser/{userId}")
    ResponseEntity<List<BookCheckoutResponseDTO>> getAllFromUser(@PathVariable UUID userId) {
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserWithId(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getForUserByTitleContaining/{userId}")
    ResponseEntity<List<BookCheckoutResponseDTO>> getAllFromUserByBookTitle(@PathVariable UUID userId,
                                                                            @RequestParam String titleSearchTerm) {
        List<BookCheckoutResponseDTO> result = bookCheckoutQueryService.getAllBookCheckoutsFromUserForBook(userId,
                titleSearchTerm);

        return ResponseEntity.ok(result);
    }
}
