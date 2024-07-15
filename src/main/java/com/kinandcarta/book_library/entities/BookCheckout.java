package com.kinandcarta.book_library.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "book_checkout")
public class BookCheckout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_item_id")
    private BookItem bookItem;

    @ManyToOne
    @JoinColumn(name = "office_name")
    private Office office;

    private LocalDate dateBorrowed;

    private LocalDate dateReturned;

    private LocalDate scheduledReturn;
}
