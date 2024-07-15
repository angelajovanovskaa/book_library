package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public final class BookStatusTransitionValidator {

    private static final Map<BookStatus, List<BookStatus>> validTransitions = new HashMap<>();

    static {

        validTransitions.put(BookStatus.REQUESTED, List.of(BookStatus.REJECTED, BookStatus.PENDING_PURCHASE));
        validTransitions.put(BookStatus.REJECTED, List.of(BookStatus.PENDING_PURCHASE));
        validTransitions.put(BookStatus.PENDING_PURCHASE, List.of(BookStatus.REJECTED, BookStatus.IN_STOCK));
    }

    public boolean isValid(BookStatus currentBookStatus, BookStatus newBookStatus) {

        List<BookStatus> allowedStatuses = validTransitions.get(currentBookStatus);

        if (allowedStatuses == null || allowedStatuses.isEmpty()) {
            return false;
        }

        return allowedStatuses.contains(newBookStatus);
    }
}
