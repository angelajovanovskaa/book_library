package com.kinandcarta.book_library.entities;

import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookStatus;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Column;
import jakarta.persistence.JoinTable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@IdClass(BookId.class)
public class Book {

    @Id
    private String isbn;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_name")
    @ToString.Exclude
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_author", joinColumns = {@JoinColumn(name = "book_isbn"), @JoinColumn(name = "office_name")},
            inverseJoinColumns =
            @JoinColumn(name = "author_id"))
    @ToString.Exclude
    private Set<Author> authors;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @ToString.Exclude
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn) && Objects.equals(office, book.office);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, office);
    }
}
