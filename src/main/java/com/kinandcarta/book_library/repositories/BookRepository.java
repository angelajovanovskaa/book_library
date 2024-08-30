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

    @Query("SELECT b FROM Book b " +
            "JOIN FETCH b.office o " +
            "JOIN FETCH b.authors a " +
            "WHERE b.office.name = :officeName")
    List<Book> findAllBooksByOfficeName(@Param("officeName") String officeName);

    @Query("SELECT b " +
            "FROM Book b " +
            "JOIN FETCH b.office o " +
            "WHERE b.bookStatus = :bookStatus " +
            "AND o.name = :officeName " +
            "AND EXISTS (" +
            "    SELECT 1 " +
            "    FROM BookItem bi " +
            "    WHERE bi.book = b " +
            "    AND bi.bookItemState = :bookItemState " +
            ")")
    List<Book> findBooksByStatusAndAvailableItems(@Param("bookStatus") BookStatus bookStatus,
                                                  @Param("bookItemState") BookItemState bookItemState,
                                                  @Param("officeName") String officeName);

    @Query("SELECT b " +
            "FROM Book b " +
            "JOIN FETCH b.office o " +
            "WHERE b.bookStatus = :bookStatus " +
            "AND o.name = :officeName " +
            "AND EXISTS (" +
            "    SELECT 1 " +
            "    FROM BookItem bi " +
            "    WHERE bi.book = b " +
            "    AND bi.bookItemState = :bookItemState " +
            ")")
    Page<Book> pagingAvailableBooks(@Param("bookStatus") BookStatus bookStatus,
                                    @Param("bookItemState") BookItemState bookItemState,
                                    @Param("officeName") String officeName,
                                    Pageable pageable);

    List<Book> findBookByBookStatusAndOfficeName(BookStatus bookStatus, String officeName);

    @Query("SELECT b FROM Book b " +
            "JOIN FETCH b.office " +
            "JOIN FETCH b.authors " +
            "WHERE b.isbn = :isbn AND b.office.name = :officeName")
    Optional<Book> findByIsbnAndOfficeName(@Param("isbn") String isbn,
                                           @Param("officeName") String officeName);

    @Query("SELECT b FROM Book b " +
            "JOIN FETCH b.office " +
            "WHERE b.title LIKE %:title% AND b.office.name = :officeName")
    List<Book> findByTitleContainingIgnoreCaseAndOfficeName(@Param("title") String title,
                                                            @Param("officeName") String officeName);

    @Query("SELECT b FROM Book b " +
            "JOIN FETCH b.office " +
            "WHERE b.language = :language AND b.office.name = :officeName")
    List<Book> findBooksByLanguageAndOfficeName(@Param("language") String language,
                                                @Param("officeName") String officeName);

    @Query(value = "SELECT b.* " +
            "FROM book b " +
            "JOIN office o ON b.office_name = o.name " +
            "WHERE b.genres @> ARRAY[:genres]::text[] " +
            "AND o.name = :officeName",
            nativeQuery = true)
    List<Book> findBooksByGenresContaining(@Param("genres") String[] genres,
                                           @Param("officeName") String officeName);

    @Modifying
    @Query("update Book b " +
            "set b.ratingFromFirm = :rating " +
            "where b.isbn = :isbn and b.office.name = :officeName")
    void updateRatingByIsbnAndOfficeName(double rating, String isbn, String officeName);

}
