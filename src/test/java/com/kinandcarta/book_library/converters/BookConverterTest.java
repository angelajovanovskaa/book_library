package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.kinandcarta.book_library.utils.BookTestData.BOOK_AUTHORS;
import static com.kinandcarta.book_library.utils.BookTestData.getBook;
import static com.kinandcarta.book_library.utils.BookTestData.getBookDTO;
import static org.assertj.core.api.Assertions.assertThat;

class BookConverterTest {

    private final BookConverter bookConverter = new BookConverter();

    @Test
    void toBookDetailsDTO_conversionIsDone_returnsBookDetailsDTO() {
        // given
        Book book = getBook();
        BookDetailsDTO bookDetailsDTO = getBookDTO();
        List<ReviewResponseDTO> reviewResponseDTOList = ReviewTestData.getReviewResponseDTOs();

        // when
        BookDetailsDTO result = bookConverter.toBookDetailsDTO(book, reviewResponseDTOList);

        // then
        assertThat(result).isEqualTo(bookDetailsDTO);
        assertThat(result.isbn()).isEqualTo(book.getIsbn());
        assertThat(result.title()).isEqualTo(book.getTitle());
        assertThat(result.description()).isEqualTo(book.getDescription());
        assertThat(result.language()).isEqualTo(book.getLanguage());
        assertThat(result.genres()).isEqualTo(book.getGenres());
        assertThat(result.totalPages()).isEqualTo(book.getTotalPages());
        assertThat(result.ratingFromWeb()).isEqualTo(book.getRatingFromWeb());
        assertThat(result.ratingFromFirm()).isEqualTo(book.getRatingFromFirm());
        assertThat(result.officeName()).isEqualTo(book.getOffice().getName());
    }

    @Test
    void toBookDisplayDTO_conversionIsDone_returnsToBookDisplay() {
        // given
        Book book = BookTestData.getBook();
        BookDisplayDTO bookDisplayDTO = BookTestData.getBookDisplayDTO();

        // when
        BookDisplayDTO result = bookConverter.toBookDisplayDTO(book);

        // then
        assertThat(result).isEqualTo(bookDisplayDTO);
        assertThat(result.isbn()).isEqualTo(book.getIsbn());
        assertThat(result.title()).isEqualTo(book.getTitle());
        assertThat(result.language()).isEqualTo(book.getLanguage());
        assertThat(result.image()).isEqualTo(book.getImage());
    }

    @Test
    void toBookId_conversionIsDone_returnsBookId() {
        // given
        BookIdDTO bookIdDTO = new BookIdDTO("9780545414654", "Bristol");

        // when
        BookId result = bookConverter.toBookId(bookIdDTO);

        // then
        assertThat(result.getIsbn()).isEqualTo(bookIdDTO.isbn());
        assertThat(result.getOffice()).isEqualTo(bookIdDTO.officeName());
    }

    @Test
    void toBookEntity_conversionIsDone_returnsBookEntity() {
        // given
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.getBookInsertRequestDTOgetBookInsertRequestDTO();
        Book book = getBook();
        Set<Author> authors = book.getAuthors();

        // when
        Book result = bookConverter.toBookEntity(bookInsertRequestDTO, authors, SharedTestData.SKOPJE_OFFICE);

        // then
        assertThat(result).isEqualTo(book);
        assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
        assertThat(result.getDescription()).isEqualTo(book.getDescription());
        assertThat(result.getLanguage()).isEqualTo(book.getLanguage());
        assertThat(result.getGenres()).isEqualTo(book.getGenres());
        assertThat(result.getTotalPages()).isEqualTo(book.getTotalPages());
        assertThat(result.getRatingFromWeb()).isEqualTo(book.getRatingFromWeb());
        assertThat(result.getRatingFromFirm()).isEqualTo(0.0);
        assertThat(result.getOffice().getName()).isEqualTo(book.getOffice().getName());
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("summary", "bookStatus", "image", "bookItems");
    }
}