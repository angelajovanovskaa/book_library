package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {

    @Id
    @SequenceGenerator(name = "author_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_sequence")
    private Long id;

    private String name;

    private String surname;

    @ManyToMany(cascade = {CascadeType.PERSIST})
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
