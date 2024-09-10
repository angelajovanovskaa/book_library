package com.kinandcarta.book_library.services;

import java.util.UUID;

public interface UserContextDataExtractor {
    UUID getAuthenticatedUserId();

    String getAuthenticatedUserOfficeName();
}