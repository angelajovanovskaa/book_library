package com.kinandcarta.book_library.DTOs;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

public record RequestedBookDTO(

        UUID id,

        Long likeCounter,

        String bookISBN,

        List<String> userEmails


) {
}
