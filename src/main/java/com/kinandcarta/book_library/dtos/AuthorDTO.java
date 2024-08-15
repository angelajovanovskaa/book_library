package com.kinandcarta.book_library.dtos;

import jakarta.validation.constraints.NotEmpty;

public record AuthorDTO(@NotEmpty String fullName) {
}
