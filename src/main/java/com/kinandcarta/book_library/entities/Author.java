package com.kinandcarta.book_library.entities;

import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

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
    @SequenceGenerator(name = "author_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_sequence")
    private Long id;

    private String name;

    private String surname;

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
