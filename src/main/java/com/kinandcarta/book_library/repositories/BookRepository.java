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
    @Query("SELECT DISTINCT b " +
            "FROM Book b " +
            "JOIN FETCH b.bookItems bi " +
            "JOIN FETCH b.authors " +
            "WHERE b.bookStatus = :bookStatus " +
            "AND EXISTS (" +
            "    SELECT 1 " +
            "    FROM BookItem bi " +
            "    WHERE bi.book = b " +
            "    AND bi.bookItemState = :bookItemState " +
            ")")
    List<Book> findBooksByStatusAndAvailableItems(@Param("bookStatus") BookStatus bookStatus,
                                                  @Param("bookItemState") BookItemState bookItemState);

    List<Book> findBookByBookStatus(BookStatus bookStatus);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findBooksByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT b " +
            "FROM Book b " +
            "JOIN FETCH b.authors " +
            "WHERE b.language = :language")
    List<Book> findByLanguage(@Param("language") String language);

    @Query(value = "SELECT * FROM book WHERE genres @> ARRAY[:genres]::text[]", nativeQuery = true)
    List<Book> findBooksByGenresContaining(@Param("genres") String[] genres);
}
