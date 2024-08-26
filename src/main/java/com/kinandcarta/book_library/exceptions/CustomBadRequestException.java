package com.kinandcarta.book_library.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomBadRequestException extends RuntimeException {
    private final String message;
}
