package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

import static com.kinandcarta.book_library.utils.BookTestData.BOOK_ISBN;
import static com.kinandcarta.book_library.utils.BookTestData.getBook;
import static com.kinandcarta.book_library.utils.BookTestData.getBookFromDifferentOffice;

@UtilityClass
public class BookItemTestData {
    public static final BookItemState BOOK_ITEM_STATE = BookItemState.AVAILABLE;
    public static final UUID BOOK_ITEM_ID = UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc51");

    public static List<BookItem> getBookItems() {
        BookItem bookItem1 = new BookItem(
                BOOK_ITEM_ID,
                BOOK_ITEM_STATE,
                getBook()
        );
        BookItem bookItem2 = new BookItem(
                UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc52"),
                BOOK_ITEM_STATE,
                getBook()
        );

        return List.of(bookItem1, bookItem2);
    }

    public static BookItem getBookItem() {
        return getBookItems().getFirst();
    }

    public static BookItem getBookItemFromDifferentOffice() {
        return new BookItem(
                UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc53"),
                BOOK_ITEM_STATE,
                getBookFromDifferentOffice()
        );
    }

    public static List<BookItemDTO> getBookItemDTOs() {
        BookItemDTO bookItemDTO1 = new BookItemDTO(
                BOOK_ISBN,
                BOOK_ITEM_ID
        );
        BookItemDTO bookItemDTO2 = new BookItemDTO(
                BOOK_ISBN,
                UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc52")
        );

        return List.of(bookItemDTO1, bookItemDTO2);
    }

    public static BookItemDTO getBookItemDTO() {
        return getBookItemDTOs().getFirst();
    }
}
