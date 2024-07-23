package com.kinandcarta.book_library.validators;

import com.kinandcarta.book_library.enums.BookStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BookStatusTransitionValidatorTest {

    private final BookStatusTransitionValidator bookStatusTransitionValidator = new BookStatusTransitionValidator();

    @Test
    void isValid_returnsTrue() {
        //given
        final BookStatus currentBookStatus = BookStatus.REQUESTED;
        final BookStatus newBookStatus = BookStatus.PENDING_PURCHASE;

        //when
        final boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        //then
        assertTrue(actualResult);
    }
}