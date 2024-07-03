package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.Book;

public interface GoogleBooksService {

    String searchBooks(String isbn);

    Book convertToBook(String json);
}
