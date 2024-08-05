package com.kinandcarta.book_library.repositories;

import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.enums.BookItemState;
import com.kinandcarta.book_library.enums.BookStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, BookId> {
    @Query("SELECT DISTINCT b " +
            "FROM Book b " +
            "WHERE b.bookStatus = :bookStatus " +
            "AND EXISTS (" +
            "    SELECT 1 " +
            "    FROM BookItem bi " +
            "    WHERE bi.book = b " +
            "    AND bi.bookItemState = :bookItemState " +
            ")")
    List<Book> findBooksByStatusAndAvailableItems(@Param("bookStatus") BookStatus bookStatus,
                                                  @Param("bookItemState") BookItemState bookItemState);

    @Query("SELECT DISTINCT b " +
            "FROM Book b " +
            "WHERE b.bookStatus = :bookStatus " +
            "AND EXISTS (" +
            "    SELECT 1 " +
            "    FROM BookItem bi " +
            "    WHERE bi.book = b " +
            "    AND bi.bookItemState = :bookItemState " +
            ")")
    Page<Book> pagingAvailableBooks(@Param("bookStatus") BookStatus bookStatus,
                                    @Param("bookItemState") BookItemState bookItemState,
                                    Pageable pageable);

    List<Book> findBookByBookStatus(BookStatus bookStatus);

    @Query("SELECT b FROM Book b JOIN FETCH b.office WHERE b.isbn = :isbn AND b.office.name = :officeName")
    Optional<Book> findByIsbnAndOffice_Name(@Param("isbn") String isbn, @Param("officeName") String officeName);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findBooksByTitleContainingIgnoreCase(String title);

    List<Book> findBooksByLanguageAndOffice_Name(String language, String officeName);

    List<Book> findByLanguage(String language);

    @Query(value = "SELECT * FROM book WHERE genres @> ARRAY[:genres]::text[]", nativeQuery = true)
    List<Book> findBooksByGenresContaining(@Param("genres") String[] genres);

    void deleteByIsbn(String isbn);

    @Modifying
    @Query("update Book b " +
            "set b.ratingFromFirm = :rating " +
            "where b.isbn = :isbn and b.office.name = :officeName")
    void updateRatingByIsbnAndOfficeName(double rating, String isbn, String officeName);
}
