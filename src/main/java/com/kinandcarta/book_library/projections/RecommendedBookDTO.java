package com.kinandcarta.book_library.projections;

import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.enums.Language;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RecommendedBookDTO (

        UUID id,

        Long likeCounter,

        String bookISBN,

        List<String> userEmails
) {
}
