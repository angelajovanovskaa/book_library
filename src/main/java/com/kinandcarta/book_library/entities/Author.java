package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fullName;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_isbn"))
    @EqualsAndHashCode.Exclude
    private Set<Book> books;

    public void addBooks(Collection<Book> books) {
        books.forEach(this::addBook);
    }

    public void addBook(Book book) {
        if (isNull(books)) {
            books = new HashSet<>();
        }
        books.add(book);

        Set<Author> authors = book.getAuthors();
        if (isNull(authors)) {
            authors = new HashSet<>();
        }
        authors.add(this);
        book.setAuthors(authors);
    }

}
