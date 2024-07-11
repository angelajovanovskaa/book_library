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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Builder
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

    private String language;

    private double ratingFromWeb = 0.0;

    private double ratingFromFirm = 0.0;

    private String image;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Type(StringArrayType.class)
    @Column(name = "genres", columnDefinition = "text[]")
    private String[] genres;

    @ManyToMany(mappedBy = "books", cascade = CascadeType.PERSIST)
    private Set<Author> authors;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
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
