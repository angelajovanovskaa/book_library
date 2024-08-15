package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.dtos.BookIdDTO;
import com.kinandcarta.book_library.dtos.BookInsertRequestDTO;
import com.kinandcarta.book_library.entities.Author;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.exceptions.OfficeNotFoundException;
import com.kinandcarta.book_library.repositories.AuthorRepository;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookManagementServiceImplTest {
    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private BookConverter bookConverter;

    @Mock
    private ReviewQueryService reviewQueryService;

    @InjectMocks
    private BookManagementServiceImpl bookService;

    @Captor
    private ArgumentCaptor<Author> authorArgumentCaptor;

    @Test
    void createBookWithAuthors_bookCreationIsValid_returnsBookDisplayDTO() {
        // given
        given(bookConverter.toBookEntity(any(), any(), any())).willReturn(BookTestData.getBook());
        given(bookRepository.save(any())).willReturn(BookTestData.getBook());
        given(officeRepository.findById(any())).willReturn(Optional.of(SharedServiceTestData.SKOPJE_OFFICE));
        given(bookConverter.toBookDisplayDTO(any())).willReturn(BookTestData.getBookDisplayDTO());

        // when
        BookDisplayDTO actualResult = bookService.createBookWithAuthors(BookTestData.getBookInsertRequestDTO());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.isbn()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(actualResult.title()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(actualResult.language()).isEqualTo(BookTestData.BOOK_LANGUAGE);
        assertThat(actualResult.image()).isEqualTo(BookTestData.BOOK_IMAGE);
    }

    @Test
    void createBookWithAuthors_authorDoesNotExists_returnsBookDisplayDTO() {
        // given
        given(officeRepository.findById(anyString())).willReturn(Optional.of(SharedServiceTestData.SKOPJE_OFFICE));
        given(authorRepository.findByFullName(anyString())).willReturn(Optional.empty());
        given(bookConverter.toBookEntity(any(), anySet(), any())).willReturn(BookTestData.getBook());
        given(bookRepository.save(any())).willReturn(BookTestData.getBook());
        given(bookConverter.toBookDisplayDTO(any())).willReturn(BookTestData.getBookDisplayDTO());

        // when
        BookDisplayDTO actualResult = bookService.createBookWithAuthors(BookTestData.getBookInsertRequestDTO());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.isbn()).isEqualTo(BookTestData.BOOK_ISBN);
        assertThat(actualResult.title()).isEqualTo(BookTestData.BOOK_TITLE);
        assertThat(actualResult.language()).isEqualTo(BookTestData.BOOK_LANGUAGE);
        assertThat(actualResult.image()).isEqualTo(BookTestData.BOOK_IMAGE);

        verify(authorRepository).save(authorArgumentCaptor.capture());

        Author capturedAuthor = authorArgumentCaptor.getValue();
        assertThat(capturedAuthor.getFullName()).isEqualTo(BookTestData.AUTHOR.getFullName());
    }

    @Test
    void createBookWithAuthors_officeDoesNotExist_throwsException() {
        // given
        BookInsertRequestDTO bookInsertRequestDTO = BookTestData.getBookInsertRequestDTO();

        given(officeRepository.findById(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatExceptionOfType(OfficeNotFoundException.class)
                .isThrownBy(() -> bookService.createBookWithAuthors(bookInsertRequestDTO))
                .withMessage("Office with name: " + SharedServiceTestData.SKOPJE_OFFICE_NAME + " not found");
    }

    @Test
    void deleteBook_bookDeleteValid_returnsBookIdDTO() {
        // given
        given(bookRepository.existsById(any())).willReturn(true);
        given(bookConverter.toBookId(any())).willReturn(BookTestData.BOOK_ID);

        // when
        BookIdDTO actualResult =
                bookService.deleteBook(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult.isbn()).isEqualTo(BookTestData.BOOK_ISBN);
    }

    @Test
    void deleteBook_bookDoesNotExist_throwsException() {
        // given
        given(bookRepository.existsById(any())).willReturn(false);
        given(bookConverter.toBookId(any())).willReturn(BookTestData.BOOK_ID);

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> bookService.deleteBook(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .withMessage("Book with ISBN: " + BookTestData.BOOK_ISBN + " not found");
    }

    @Test
    void setBookStatusInStock_validTransition_returnsBookDetailsDTO() {
        // given
        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString())).willReturn(
                Optional.of(BookTestData.getBook()));
        given(bookConverter.toBookDetailsDTO(any(), any())).willReturn(BookTestData.getBookDetailsDTO());
        given(reviewQueryService.getTopReviewsForDisplayInBookView(anyString(), anyString())).willReturn(
                ReviewTestData.getReviewResponseDTOs());

        // when
        BookDetailsDTO actualResult = bookService.setBookStatusInStock(BookTestData.BOOK_ID_DTO);

        // then
        assertThat(actualResult).isEqualTo(BookTestData.getBookDetailsDTO());
    }

    @Test
    void setBookStatusInStock_bookDoesNotExist_throwsException() {
        // given
        given(bookRepository.findByIsbnAndOfficeName(anyString(), anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.setBookStatusInStock(BookTestData.BOOK_ID_DTO))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ISBN: " + BookTestData.BOOK_ISBN + " not found");
    }
}
