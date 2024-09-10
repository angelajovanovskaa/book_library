package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.User;
import com.kinandcarta.book_library.services.UserContextDataExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation that provides access to information about the currently authenticated user.
 * This service allows retrieval of the user's attributes based on the current security context.
 * Implements {@link UserContextDataExtractor}
 * Uses {@link SecurityContextHolder} to access the currently authenticated user.
 */
@Service
public class UserContextDataExtractorImpl implements UserContextDataExtractor {

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * @return the UUID of the authenticated user.
     */
    @Override
    public UUID getAuthenticatedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return user.getId();
    }

    /**
     * Retrieves the office name of the currently authenticated user.
     *
     * @return the name of the office associated with the authenticated user.
     */
    @Override
    public String getAuthenticatedUserOfficeName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Office office = user.getOffice();

        return office.getName();
    }
}
