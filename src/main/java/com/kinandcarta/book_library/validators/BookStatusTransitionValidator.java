package com.kinandcarta.book_library.validators;

import com.kinandcarta.book_library.enums.BookStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * {@code BookStatusTransitionValidator} class provides functionality to validate
 * the transitions between different states of a book.
 */
@Component
public final class BookStatusTransitionValidator {

    private static final Map<BookStatus, List<BookStatus>> VALID_STATUS_TRANSITIONS = Map.ofEntries(
            entry(BookStatus.REQUESTED, List.of(BookStatus.REJECTED, BookStatus.PENDING_PURCHASE)),
            entry(BookStatus.REJECTED, List.of(BookStatus.PENDING_PURCHASE)),
            entry(BookStatus.PENDING_PURCHASE, List.of(BookStatus.REJECTED, BookStatus.IN_STOCK))
    );

    /**
     * Validates if the transition from current status to new status is valid.
     *
     * @param currentBookStatus the current status of the book
     * @param newBookStatus     the new status to transition to
     * @return true if the transition is valid, false otherwise
     */
    public boolean isValid(BookStatus currentBookStatus, BookStatus newBookStatus) {

        List<BookStatus> allowedStatuses = VALID_STATUS_TRANSITIONS.get(currentBookStatus);

        if (CollectionUtils.isEmpty(allowedStatuses)) {
            return false;
        }

        return allowedStatuses.contains(newBookStatus);
    }
}
