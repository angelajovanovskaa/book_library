package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class BookReturnDateCalculatorServiceImplTest {
    private static final Office SKOPJE_OFFICE = new Office("Skopje");

    @InjectMocks
    private BookReturnDateCalculatorServiceImpl bookReturnDateCalculatorService;

    @Test
    void calculateReturnDateOfBookItem_theCalculationIsDone_returnsLocalDate() {
        // given
        Book book = getBook();

        // when
        LocalDate result = bookReturnDateCalculatorService.calculateReturnDateOfBookItem(book);

        // then
        assertThat(result).isEqualTo(LocalDate.now().plusDays(5));
    }

    private Book getBook() {
        String[] genres = {String.valueOf(Genre.BIOGRAPHY), String.valueOf(Genre.HISTORY)};

        Author author = new Author(UUID.fromString("3fa01d29-333a-4b1a-a620-bcb4a0ea5acc"), "AA AA", new HashSet<>());


        Book book = new Book("1111", SKOPJE_OFFICE, "Homo sapiens2", "book description", "some summary", 123,
                String.valueOf(Language.ENGLISH), 10.0, 9.0, "https://google.com", BookStatus.PENDING_PURCHASE,
                genres, new HashSet<>(), new ArrayList<>());

        author.addBook(book);
        book.getAuthors().add(author);

        return book;
    }
}
