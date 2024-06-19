package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    private String ISBN;

    private String title;

    private String description;

    private String summary;

    private int totalPages;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Value("${0.0}")
    private double ratingFromWeb;

    @Value("${0.0}")
    private double ratingFromFirm;

    private String image;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Enumerated(EnumType.STRING)
    @Lob
    private Genre[] genres;

    @ManyToMany(mappedBy = "books",cascade = CascadeType.PERSIST)
    private Set<Author> authors;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<BookItem> bookItems;


    public void addGenres(Collection<Genre> genres) {
        if (nonNull(genres) && !genres.isEmpty()) {
            genres.forEach(this::addGenre);
        }
    }

    public void addGenre(Genre genre) {
        if (isNull(this.genres)) {
            this.genres = new Genre[1];
        }
        int n = 0;
        this.genres[n] = genre;
    }

    public void addBookItems(Collection<BookItem> bookItems) {
        if (nonNull(bookItems) && !bookItems.isEmpty()) {
            bookItems.forEach(this::addBookItem);
        }
    }

    private void addBookItem(BookItem bookItem) {
        if (isNull(this.bookItems)) {
            this.bookItems = new ArrayList<>();
        }
        this.bookItems.add(bookItem);
        bookItem.setBook(this);
    }

    public void setGenres(Object[] array) {
    }
}
