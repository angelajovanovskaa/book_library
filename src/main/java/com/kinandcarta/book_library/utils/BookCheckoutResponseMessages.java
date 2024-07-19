package com.kinandcarta.book_library.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BookCheckoutResponseMessages {
    public static final String BOOK_ITEM_BORROWED_RESPONSE = "You have successfully borrowed the book";

    public static final String BOOK_ITEM_RETURN_OVERDUE_RESPONSE =
            "The book return is overdue next time be more careful about the scheduled return date.";

    public static final String BOOK_ITEM_RETURN_ON_TIME_RESPONSE = "The book is returned on the scheduled return date.";

    public static final String BOOK_ITEM_RETURN_BEFORE_SCHEDULE_RESPONSE =
            "The book is returned before the scheduled return date.";

}
