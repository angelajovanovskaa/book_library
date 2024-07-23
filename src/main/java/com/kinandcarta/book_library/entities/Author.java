package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

import static java.util.Objects.isNull;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fullName;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = {@JoinColumn(name = "book_isbn"), @JoinColumn(name = "office_name")})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
