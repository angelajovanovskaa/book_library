package com.kinandcarta.book_library.dtos;

import com.kinandcarta.book_library.enums.BookStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RequestedBookChangeStatusRequestDTO(
        @NotNull
        UUID requestedBookId,

        @NotNull
        BookStatus newBookStatus
) {
}
