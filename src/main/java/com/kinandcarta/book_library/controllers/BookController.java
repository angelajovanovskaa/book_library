package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.services.BookManagementService;
import com.kinandcarta.book_library.services.BookQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/books")
public class BookController {

    private final BookQueryService bookQueryService;
    private final BookManagementService bookManagementService;

    @GetMapping
    ResponseEntity<List<BookDisplayDTO>> getBooks(@RequestParam @NotBlank String officeName) {
        List<BookDisplayDTO> result =
                bookQueryService.getAllBooks(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-book")
    ResponseEntity<BookDetailsDTO> getBook(@RequestParam @NotBlank String isbn,
                                           @RequestParam @NotBlank String officeName) {
        BookDetailsDTO result = bookQueryService.getBookByIsbn(isbn, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/available")
    ResponseEntity<List<BookDisplayDTO>> getAvailableBooks(@RequestParam @NotBlank String officeName) {
        List<BookDisplayDTO> result = bookQueryService.getAvailableBooks(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/paginated")
    ResponseEntity<Page<BookDisplayDTO>> getPaginatedBooks(
            @RequestParam @NotBlank String officeName, @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) {
        Page<BookDisplayDTO> result =
                bookQueryService.getPaginatedAvailableBooks(pageNumber, pageSize,
                        officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/requested")
    ResponseEntity<List<BookDisplayDTO>> getRequestedBooks(@RequestParam @NotBlank String officeName) {
        List<BookDisplayDTO> result = bookQueryService.getRequestedBooks(officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-title")
    ResponseEntity<List<BookDisplayDTO>> getBooksBySearchTitle(@RequestParam @NotBlank String titleSearchTerm,
                                                               @RequestParam @NotBlank String officeName) {
        List<BookDisplayDTO> result = bookQueryService.getBooksByTitle(titleSearchTerm, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-language")
    ResponseEntity<List<BookDisplayDTO>> getBooksByLanguage(@RequestParam @NotBlank String language,
                                                            @RequestParam @NotBlank String officeName) {
        List<BookDisplayDTO> result = bookQueryService.getBooksByLanguage(language, officeName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-genres")
    ResponseEntity<List<BookDisplayDTO>> getBooksByGenres(@RequestParam @NotEmpty @Size(min = 1) List< @NotBlank String> genres,
                                                          @RequestParam @NotBlank String officeName) {
        String[] genresArray = genres.toArray(new String[0]);

        List<BookDisplayDTO> result = bookQueryService.getBooksByGenresContaining(genresArray, officeName);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/insert-book")
    public ResponseEntity<BookDisplayDTO> createBook(
            @Valid @RequestBody BookInsertRequestDTO bookInsertRequestDTO) {
        BookDisplayDTO response = bookManagementService.createBookWithAuthors(bookInsertRequestDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BookIdDTO> deleteBook(@RequestParam @NotBlank String isbn,
                                                @RequestParam @NotBlank String officeName) {
        BookIdDTO response = bookManagementService.deleteBook(isbn, officeName);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/set-book-in-stock")
    public ResponseEntity<BookDetailsDTO> setBookStatus(@RequestParam @NotBlank String isbn,
                                                        @RequestParam @NotBlank String officeName) {
        BookIdDTO bookIdDTO = new BookIdDTO(isbn, officeName);
        BookDetailsDTO response = bookManagementService.setBookStatusInStock(bookIdDTO);

        return ResponseEntity.ok(response);
    }
}
