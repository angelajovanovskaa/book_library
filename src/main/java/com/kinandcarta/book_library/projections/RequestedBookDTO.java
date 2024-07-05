package com.kinandcarta.book_library.projections;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RequestedBookDTO(

        UUID id,

        Long likeCounter,

        String bookISBN,

        List<String> userEmails
) {
}
