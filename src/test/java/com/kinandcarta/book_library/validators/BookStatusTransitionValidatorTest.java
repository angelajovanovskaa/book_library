package com.kinandcarta.book_library.validators;

import com.kinandcarta.book_library.enums.BookStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookStatusTransitionValidatorTest {

    private final BookStatusTransitionValidator bookStatusTransitionValidator = new BookStatusTransitionValidator();

    @Test
    void isValid_changeBookStatusFromRequestedToPendingPurchase_returnsTrue() {
        // given
        final BookStatus currentBookStatus = BookStatus.REQUESTED;
        final BookStatus newBookStatus = BookStatus.PENDING_PURCHASE;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    void isValid_changeBookStatusFromRequestedToRejected_returnsTrue() {
        // given
        final BookStatus currentBookStatus = BookStatus.REQUESTED;
        final BookStatus newBookStatus = BookStatus.REJECTED;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    void isValid_changeBookStatusFromRequestedToInStock_returnsFalse() {
        // given
        final BookStatus currentBookStatus = BookStatus.REQUESTED;
        final BookStatus newBookStatus = BookStatus.IN_STOCK;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isFalse();
    }

    @Test
    void isValid_changeBookStatusFromRejectedToPendingPurchase_returnsTrue() {
        // given
        final BookStatus currentBookStatus = BookStatus.REJECTED;
        final BookStatus newBookStatus = BookStatus.PENDING_PURCHASE;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    void isValid_changeBookStatusFromRejectedToInStock_returnsFalse() {
        // given
        final BookStatus currentBookStatus = BookStatus.REJECTED;
        final BookStatus newBookStatus = BookStatus.IN_STOCK;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isFalse();
    }

    @Test
    void isValid_changeBookStatusFromPendingPurchaseToRejected_returnsTrue() {
        // given
        final BookStatus currentBookStatus = BookStatus.PENDING_PURCHASE;
        final BookStatus newBookStatus = BookStatus.REJECTED;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    void isValid_changeBookStatusFromPendingPurchaseToInStock_returnsTrue() {
        // given
        final BookStatus currentBookStatus = BookStatus.PENDING_PURCHASE;
        final BookStatus newBookStatus = BookStatus.IN_STOCK;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isTrue();
    }

    @Test
    void isValid_changeBookStatusFromPendingPurchaseToRequested_returnsFalse() {
        // given
        final BookStatus currentBookStatus = BookStatus.PENDING_PURCHASE;
        final BookStatus newBookStatus = BookStatus.REQUESTED;

        // when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isFalse();
    }
}