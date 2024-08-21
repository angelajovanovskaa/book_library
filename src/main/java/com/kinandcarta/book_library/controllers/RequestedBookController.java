package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requested-books")
public class RequestedBookController {
    private final RequestedBookQueryService requestedBookQueryService;
    private final RequestedBookManagementService requestedBookManagementService;

    @GetMapping
    ResponseEntity<List<RequestedBookResponseDTO>> getRequestedBooks(@RequestParam @NotBlank String officeName) {
        List<RequestedBookResponseDTO> response =
                requestedBookQueryService.getRequestedBooksByOfficeName(officeName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtered")
    ResponseEntity<List<RequestedBookResponseDTO>> getRequestedBooksByBookStatusAndOfficeName(
            @RequestParam @NotBlank String status, @RequestParam @NotBlank String officeName) {
        List<RequestedBookResponseDTO> response =
                requestedBookQueryService.getRequestedBooksByBookStatusAndOfficeName(BookStatus.valueOf(status),
                        officeName);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    ResponseEntity<String> deleteRequestedBook(@RequestParam @NotBlank String isbn,
                                               @RequestParam @NotBlank String officeName) {
        String deletedBookIsbn =
                requestedBookManagementService.deleteRequestedBookByBookIsbnAndOfficeName(isbn, officeName);

        return ResponseEntity.ok(deletedBookIsbn);
    }

    @PatchMapping("/change-status")
    ResponseEntity<RequestedBookResponseDTO> changeBookStatus(
            @RequestBody RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO) {
        RequestedBookResponseDTO response =
                requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/handle-like")
    ResponseEntity<RequestedBookResponseDTO> handleRequestedBookLike(
            @RequestBody RequestedBookRequestDTO requestedBookRequestDTO) {
        RequestedBookResponseDTO response =
                requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO);

        return ResponseEntity.ok(response);
    }
}
