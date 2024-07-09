package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByBookStatusAndBookItems_BookItemState(BookStatus bookStatus, BookItemState bookItemState);
    List<Book> findBookByBookStatus(BookStatus bookStatus);
    Optional<Book> findByISBN(String isbn);
    List<Book> findBooksByTitle(String title);
    List<Book> findByLanguage(String language);
    @Query(value = "SELECT * FROM book WHERE genres @> ARRAY[:genres]::text[]", nativeQuery = true)
    List<Book> findBooksByGenresContaining(@Param("genres") String[] genres);


}
