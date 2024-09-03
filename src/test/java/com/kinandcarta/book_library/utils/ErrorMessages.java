package com.kinandcarta.book_library.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public static final String MUST_NOT_BE_BLANK = "must not be blank";
    public static final String MUST_NOT_BE_NULL = "must not be null";
    public static final String ISBN_NOT_PRESENT = "Required parameter 'isbn' is not present";
    public static final String OFFICE_NAME_NOT_PRESENT = "Required parameter 'officeName' is not present.";
    public static final String FULL_NAME_NOT_PRESENT = "Required parameter 'fullName' is not present.";
    public static final String USER_ID_NOT_PRESENT = "Required parameter 'userId' is not present.";
    public static final String USER_ID_FAIL_CONVERT = "Failed to convert 'userId' with value: '%s'";
    public static final String EMAIL_BAD_FORMAT = "must be a well-formed email address";
}
