package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthorDTO(@NotBlank String fullName) {
}
