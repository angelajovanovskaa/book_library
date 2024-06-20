package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.enums.Genre;
import com.kinandcarta.book_library.enums.Language;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;


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

    private double ratingFromWeb = 0.0;

    private double ratingFromFirm = 0.0;

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
        if (isNotEmpty(genres)) {
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
        if (isNotEmpty(bookItems)) {
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
