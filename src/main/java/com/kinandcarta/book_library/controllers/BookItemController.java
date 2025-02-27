package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.services.BookItemManagementService;
import com.kinandcarta.book_library.services.BookItemQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book-items")
@Validated
public class BookItemController {

    private final BookItemQueryService bookItemQueryService;
    private final BookItemManagementService bookItemManagementService;

    @GetMapping
    ResponseEntity<List<BookItemDTO>> getBookItems(@RequestParam @NotBlank String isbn,
                                                   @RequestParam @NotBlank String officeName) {
        List<BookItemDTO> result = bookItemQueryService.getBookItemsByBookIsbn(isbn, officeName);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/insert")
    ResponseEntity<BookItemDTO> createBookItem(@Valid @RequestBody BookIdDTO bookIdDTO) {
        BookItemDTO response = bookItemManagementService.insertBookItem(bookIdDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/delete/{bookItemId}")
    ResponseEntity<UUID> deleteBookItem(@PathVariable UUID bookItemId) {
        UUID response = bookItemManagementService.deleteById(bookItemId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/report-damage/{bookItemId}")
    ResponseEntity<String> reportBookItemAsDamaged(@PathVariable UUID bookItemId) {
        String response = bookItemManagementService.reportBookItemAsDamaged(bookItemId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/report-lost/{bookItemId}")
    ResponseEntity<String> reportBookItemAsLost(@PathVariable UUID bookItemId) {
        String response = bookItemManagementService.reportBookItemAsLost(bookItemId);

        return ResponseEntity.ok(response);
    }
}
