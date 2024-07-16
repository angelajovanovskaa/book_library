package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "book_checkout")
public class BookCheckout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "book_item_id")
    private BookItem bookItem;

    private LocalDate dateBorrowed;

    private LocalDate dateReturned;

    private LocalDate scheduledReturnDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCheckout that = (BookCheckout) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
