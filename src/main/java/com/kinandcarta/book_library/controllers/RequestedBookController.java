package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.RequestedBookChangeStatusRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookRequestDTO;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.services.RequestedBookManagementService;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class RequestedBookController {
    private final RequestedBookQueryService requestedBookQueryService;
    private final RequestedBookManagementService requestedBookManagementService;

    @GetMapping
    ResponseEntity<List<RequestedBookResponseDTO>> getRequestedBooks(@RequestParam @NotBlank String officeName) {
        List<RequestedBookResponseDTO> response =
                requestedBookQueryService.getRequestedBooksByOfficeName(officeName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-book-status")
    ResponseEntity<List<RequestedBookResponseDTO>> getRequestedBooksByStatusAndOffice(
            @RequestParam @NotBlank String officeName, @RequestParam @NotNull BookStatus status) {
        List<RequestedBookResponseDTO> response =
                requestedBookQueryService.getRequestedBooksByBookStatusAndOfficeName(status, officeName);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-book-status")
    ResponseEntity<RequestedBookResponseDTO> changeBookStatus(
            @RequestBody @Valid RequestedBookChangeStatusRequestDTO requestedBookChangeStatusRequestDTO) {
        RequestedBookResponseDTO response =
                requestedBookManagementService.changeBookStatus(requestedBookChangeStatusRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/handle-like")
    ResponseEntity<RequestedBookResponseDTO> handleRequestedBookLike(
            @RequestBody @Valid RequestedBookRequestDTO requestedBookRequestDTO) {
        RequestedBookResponseDTO response =
                requestedBookManagementService.handleRequestedBookLike(requestedBookRequestDTO);

        return ResponseEntity.ok(response);
    }
}
