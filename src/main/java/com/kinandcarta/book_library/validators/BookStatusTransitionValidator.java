package com.kinandcarta.book_library.validators;

import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class BookStatusTransitionValidator {

    private static final Map<BookStatus, List<BookStatus>> validTransitions = new HashMap<>();

    static {

        validTransitions.put(BookStatus.REQUESTED, List.of(BookStatus.REJECTED, BookStatus.PENDING_PURCHASE));
        validTransitions.put(BookStatus.REJECTED, List.of(BookStatus.PENDING_PURCHASE));
        validTransitions.put(BookStatus.PENDING_PURCHASE, List.of(BookStatus.REJECTED, BookStatus.IN_STOCK));
    }

    /**
     * Validates if the transition from current status to new status is valid.
     *
     * @param currentBookStatus the current status of the book
     * @param newBookStatus     the new status to transition to
     * @return true if the transition is valid, false otherwise
     */
    public boolean isValid(BookStatus currentBookStatus, BookStatus newBookStatus) {

        List<BookStatus> allowedStatuses = validTransitions.get(currentBookStatus);

        if (allowedStatuses == null || allowedStatuses.isEmpty()) {
            return false;
        }

        return allowedStatuses.contains(newBookStatus);
    }
}
