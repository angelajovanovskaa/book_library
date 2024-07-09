package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookItemState;

import jakarta.persistence.*;

import lombok.*;

import java.util.Collection;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private BookItemState bookItemState;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JoinColumn(name = "book_isbn")
    private Book book;

    public void addBook(Book book) {
        if (nonNull(book)) {
            this.book = book;
            Collection<BookItem> bookItemStates = book.getBookItems();
            if (isNotEmpty(bookItemStates)) {
                bookItemStates.add(this);
                book.addBookItems(bookItemStates);
            }
        }
    }

}
