package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
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

    @Type(StringArrayType.class)
    @Column(
            name = "genres",
            columnDefinition = "text[]"
    )
    private Genre[] genres;

    @ManyToMany(mappedBy = "books", cascade = CascadeType.PERSIST)
    private Set<Author> authors;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
    private List<BookItem> bookItems;


    public void replaceGenres(Collection<Genre> genres) {
        if (!isNull(this.genres) && this.genres.length != 0) {
            List<Genre> currentGenres = new ArrayList<>(Arrays.asList(this.genres));
            currentGenres.addAll(genres);

            List<Genre> distinctGenres = currentGenres.stream().distinct().toList();

            this.genres = distinctGenres.toArray(new Genre[0]);
        } else {
            this.genres = new Genre[genres.size()];
            this.genres = genres.toArray(new Genre[0]);
        }
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

}
