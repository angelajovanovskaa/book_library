package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookStatus;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(BookId.class)
public class Book {

    @Id
    private String isbn;

    @Id
    @ManyToOne
    @JoinColumn(name = "office_name")
    private Office office;

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
