package com.kinandcarta.book_library.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BookCheckoutResponseMessages {
    public static final String BOOK_ITEM_BORROWED_RESPONSE = "You have successfully borrowed the book";

    public static final String BOOK_ITEM_RETURN_OVERDUE_RESPONSE = "The book return is overdue the scheduled date.";

    public static final String BOOK_ITEM_RETURN_ON_TIME_RESPONSE = "The book is returned on time.";
}
