package com.kinandcarta.book_library.DTOs;

import java.util.List;
import java.util.UUID;

public record RequestedBookDTO(

        UUID id,

        Long likeCounter,

        String bookISBN,

        String title,

        String language,

        String image,

        List<String> authorsFullName,

        List<String> userEmails
) {
}
