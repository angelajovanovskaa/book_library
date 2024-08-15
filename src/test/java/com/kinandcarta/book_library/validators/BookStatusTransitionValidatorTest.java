package com.kinandcarta.book_library.validators;

import com.kinandcarta.book_library.enums.BookStatus;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class BookStatusTransitionValidatorTest {

    private final BookStatusTransitionValidator bookStatusTransitionValidator = new BookStatusTransitionValidator();

    @ParameterizedTest
    @MethodSource("provideParametersForTest")
    void isValid_testBookStatusTransitions_returnsBoolean(BookStatus currentBookStatus, BookStatus newBookStatus,
                                                          boolean expectedResult) {
        // when
        boolean actualResult = bookStatusTransitionValidator.isValid(currentBookStatus, newBookStatus);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideParametersForTest() {
        return Stream.of(
                Arguments.of(BookStatus.REQUESTED, BookStatus.PENDING_PURCHASE, true),
                Arguments.of(BookStatus.REQUESTED, BookStatus.REJECTED, true),
                Arguments.of(BookStatus.REQUESTED, BookStatus.IN_STOCK, false),
                Arguments.of(BookStatus.REQUESTED, BookStatus.CURRENTLY_UNAVAILABLE, false),
                Arguments.of(BookStatus.PENDING_PURCHASE, BookStatus.REJECTED, true),
                Arguments.of(BookStatus.PENDING_PURCHASE, BookStatus.IN_STOCK, true),
                Arguments.of(BookStatus.PENDING_PURCHASE, BookStatus.REQUESTED, false),
                Arguments.of(BookStatus.PENDING_PURCHASE, BookStatus.CURRENTLY_UNAVAILABLE, false),
                Arguments.of(BookStatus.REJECTED, BookStatus.PENDING_PURCHASE, true),
                Arguments.of(BookStatus.REJECTED, BookStatus.REQUESTED, false),
                Arguments.of(BookStatus.REJECTED, BookStatus.IN_STOCK, false),
                Arguments.of(BookStatus.REJECTED, BookStatus.CURRENTLY_UNAVAILABLE, false),
                Arguments.of(BookStatus.IN_STOCK, BookStatus.CURRENTLY_UNAVAILABLE, true),
                Arguments.of(BookStatus.IN_STOCK, BookStatus.REQUESTED, false),
                Arguments.of(BookStatus.IN_STOCK, BookStatus.PENDING_PURCHASE, false),
                Arguments.of(BookStatus.IN_STOCK, BookStatus.REJECTED, false),
                Arguments.of(BookStatus.CURRENTLY_UNAVAILABLE, BookStatus.IN_STOCK, true),
                Arguments.of(BookStatus.CURRENTLY_UNAVAILABLE, BookStatus.REQUESTED, false),
                Arguments.of(BookStatus.CURRENTLY_UNAVAILABLE, BookStatus.PENDING_PURCHASE, false),
                Arguments.of(BookStatus.CURRENTLY_UNAVAILABLE, BookStatus.REJECTED, false)
        );
    }
}