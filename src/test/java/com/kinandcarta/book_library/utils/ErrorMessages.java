package com.kinandcarta.book_library.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public static final String MUST_NOT_BE_BLANK = "must not be blank";
    public static final String MUST_NOT_BE_EMPTY = "must not be empty";
    public static final String ISBN_NOT_PRESENT = "Required parameter 'isbn' is not present.";
    public static final String OFFICE_NAME_NOT_PRESENT = "Required parameter 'officeName' is not present.";
    public static final String TITLE_SEARCH_TERM_NOT_PRESENT = "Required parameter 'titleSearchTerm' is not present.";
    public static final String LANGUAGE_NOT_PRESENT = "Required parameter 'language' is not present.";
    public static final String GENRES_NOT_PRESENT = "Required parameter 'genres' is not present.";
    public static final String BOOK_NOT_FOUND_EXCEPTION_MESSAGE = "Book with ISBN: invalidISBN not found";
}
