package com.kinandcarta.book_library.dtos;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record RequestedBookDTO(

        UUID id,

        Date requestedDate,

        Long likeCounter,

        String bookISBN,

        String title,

        String language,

        String image,

        String[] genres,

        List<String> authorsFullName,

        List<String> userEmails
) {
}
