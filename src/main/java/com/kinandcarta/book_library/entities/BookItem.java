package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class BookItem {

    @Id
    @SequenceGenerator(name = "book_item_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_id_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookState bookState;

    private String barcode;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "book_isbn")
    private Book book;

    public void addBook(Book book) {
        if (nonNull(book)) {
            this.book = book;
            Collection<BookItem> bookItems = book.getBookItems();
            bookItems.add(this);
            book.addBookItems(bookItems);
        }
    }
}
