package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookItemState;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class BookItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private BookItemState bookItemState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    @JoinColumn(name = "office_name")
    @ToString.Exclude
    private Book book;

    public void addBook(Book book) {
        if (nonNull(book)) {
            this.book = book;
            Collection<BookItem> bookItems = book.getBookItems();
            if (isNotEmpty(bookItems)) {
                bookItems.add(this);
                book.addBookItems(bookItems);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookItem bookItem = (BookItem) o;
        return Objects.equals(id, bookItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
