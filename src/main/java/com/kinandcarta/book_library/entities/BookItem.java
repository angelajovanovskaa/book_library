package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookItemState;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @ManyToOne()
    @ToString.Exclude
    @JoinColumn(name = "book_isbn")
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

}
