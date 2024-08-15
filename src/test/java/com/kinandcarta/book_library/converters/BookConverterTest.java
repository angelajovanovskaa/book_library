package com.kinandcarta.book_library.converters;

import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.keys.BookId;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookConverterTest {

    private final BookConverter bookConverter = new BookConverter();

    @Test
    void toBookDetailsDTO_convertsBookToBookDetailsDTOWithReviewsActionIsValid_returnsBookDetailsDTO() {
        // given

        // when
        BookDetailsDTO actualResult =
                bookConverter.toBookDetailsDTO(BookTestData.getBook(), ReviewTestData.getReviewResponseDTOs());

        // then
        assertThat(actualResult).isEqualTo(BookTestData.getBookDetailsDTO());
        assertThat(actualResult.isbn()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(actualResult.title()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(actualResult.description()).isEqualTo(BookTestData.BOOK_DESCRIPTION);
        assertThat(actualResult.language()).isEqualTo(BookTestData.BOOK_LANGUAGE);
        assertThat(actualResult.genres()).isEqualTo(BookTestData.BOOK_GENRES);
        assertThat(actualResult.totalPages()).isEqualTo(BookTestData.BOOK_TOTAL_PAGES);
        assertThat(actualResult.ratingFromWeb()).isEqualTo(BookTestData.BOOK_RATING_FROM_WEB);
        assertThat(actualResult.ratingFromFirm()).isEqualTo(BookTestData.BOOK_RATING_FROM_FIRM);
        assertThat(actualResult.officeName()).isEqualTo(SharedTestData.SKOPJE_OFFICE_NAME);
    }

    @Test
    void toBookDetailsDTO_convertsBookToBookDetailsDTOActionIsValid_returnsBookDetailsDTO() {
        // given

        // when
        BookDisplayDTO actualResult = bookConverter.toBookDisplayDTO(BookTestData.getBook());

        // then
        assertThat(actualResult).isEqualTo(BookTestData.getBookDisplayDTO());
        assertThat(actualResult.isbn()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(actualResult.title()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(actualResult.language()).isEqualTo(BookTestData.BOOK_LANGUAGE);
        assertThat(actualResult.image()).isEqualTo(BookTestData.BOOK_IMAGE);
    }

    @Test
    void toBookId_convertsBookIdToBookIdDTOActionIsValid_returnsBookId() {
        // given

        // when
        BookId result = bookConverter.toBookId(BookTestData.BOOK_ID_DTO);

        // then
        assertThat(result.getIsbn()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(result.getOffice()).isEqualTo(SharedTestData.SKOPJE_OFFICE_NAME);
    }

    @Test
    void toBook_convertsBookInsertRequestDTOToBookActionIsValid_returnsBook() {
        // given

        // when
        Book actualResult = bookConverter.toBookEntity(BookTestData.getBookInsertRequestDTO(), BookTestData.AUTHORS,
                SharedTestData.SKOPJE_OFFICE);

        // then
        assertThat(actualResult).isEqualTo(BookTestData.getBook());
        assertThat(actualResult.getIsbn()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(actualResult.getTitle()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(actualResult.getDescription()).isEqualTo(BookTestData.BOOK_DESCRIPTION);
        assertThat(actualResult.getLanguage()).isEqualTo(BookTestData.BOOK_LANGUAGE);
        assertThat(actualResult.getGenres()).isEqualTo(BookTestData.BOOK_GENRES);
        assertThat(actualResult.getTotalPages()).isEqualTo(BookTestData.BOOK_TOTAL_PAGES);
        assertThat(actualResult.getRatingFromWeb()).isEqualTo(BookTestData.BOOK_RATING_FROM_WEB);
        assertThat(actualResult.getRatingFromFirm()).isEqualTo(0.0);
        assertThat(actualResult.getOffice()).isEqualTo(SharedTestData.SKOPJE_OFFICE);
        assertThat(actualResult).hasNoNullFieldsOrPropertiesExcept("summary", "bookStatus", "image", "bookItems");
    }
}