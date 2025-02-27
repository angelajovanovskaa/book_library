package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.BookItemDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.enums.BookItemState;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class BookItemTestData {
    public static final UUID BOOK_ITEM_ID = UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc51");
    public static final UUID BOOK_ITEM_DIFFERENT_OFFICE_ID = UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc53");

    public static List<BookItem> getBookItems() {
        BookItem bookItem1 = new BookItem(
                BOOK_ITEM_ID,
                BookItemState.AVAILABLE,
                BookTestData.getBook()
        );
        BookItem bookItem2 = new BookItem(
                UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc52"),
                BookItemState.AVAILABLE,
                BookTestData.getBook()
        );

        return List.of(bookItem1, bookItem2);
    }

    public static BookItem getBookItem() {
        return getBookItems().getFirst();
    }

    public static BookItem getBookItemFromDifferentOffice() {
        Book book = BookTestData.getBook();
        book.setOffice(SharedServiceTestData.SOFIJA_OFFICE);
        return new BookItem(
                BOOK_ITEM_DIFFERENT_OFFICE_ID,
                BookItemState.BORROWED,
                book
        );
    }

    public static List<BookItemDTO> getBookItemDTOs() {
        BookItemDTO bookItemDTO1 = new BookItemDTO(
                BookTestData.BOOK_ISBN,
                BOOK_ITEM_ID
        );
        BookItemDTO bookItemDTO2 = new BookItemDTO(
                BookTestData.BOOK_ISBN,
                UUID.fromString("d8f6930a-43e4-4668-a8b2-6e1e208cfc52")
        );

        return List.of(bookItemDTO1, bookItemDTO2);
    }

    public static BookItemDTO getBookItemDTO() {
        return getBookItemDTOs().getFirst();
    }
}