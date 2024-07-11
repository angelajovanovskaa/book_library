package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.enums.BookStatus;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Type;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;


import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {

    @Id
    private String isbn;

    private String title;

    private String description;

    private String summary;

    private int totalPages;

    private String language;

    private double ratingFromWeb = 0.0;

    private double ratingFromFirm = 0.0;

    private String image;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Type(StringArrayType.class)
    @Column(name = "genres", columnDefinition = "text[]")
    private String[] genres;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "book_isbn"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private List<BookItem> bookItems;

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
