package com.kinandcarta.book_library.dtos;

import lombok.Builder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
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
