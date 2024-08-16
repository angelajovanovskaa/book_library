package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.BookConverter;
import com.kinandcarta.book_library.dtos.BookDetailsDTO;
import com.kinandcarta.book_library.dtos.BookDisplayDTO;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.BookNotFoundException;
import com.kinandcarta.book_library.repositories.BookRepository;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookItemTestData;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookQueryServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookConverter bookConverter;

    @Mock
    private ReviewQueryService reviewQueryService;

    @InjectMocks
    private BookQueryServiceImpl bookService;

    @Test
    void getAllBooks_atLeastOneBookExists_returnsListOfBookDisplayDTO() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookRepository.findAllBooksByOfficeName(any())).willReturn(BookTestData.getBooks());
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> actualResult = bookService.getAllBooks(SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void getPaginatedAvailableBooks_atLeastOneBookExists_returnsPageOfBookDisplayDTO() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookRepository.pagingAvailableBooks(BookStatus.REQUESTED, BookItemTestData.BOOK_ITEM_STATE,
                SharedServiceTestData.SKOPJE_OFFICE.getName(),
                PageRequest.of(SharedServiceTestData.PAGE_NUMBER, SharedServiceTestData.PAGE_SIZE)))
                .willReturn(new PageImpl<>(BookTestData.getBooks()));

        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        Page<BookDisplayDTO> actualResult = bookService
                .getPaginatedAvailableBooks(BookStatus.REQUESTED, BookItemTestData.BOOK_ITEM_STATE,
                        SharedServiceTestData.PAGE_NUMBER, SharedServiceTestData.PAGE_SIZE,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        //  then
        assertThat(actualResult.getContent()).containsExactlyElementsOf(bookDisplayDTOs);
    }

    @Test
    void getAvailableBooks_atLeastOneAvailableBookExists_returnsListOfBookDisplayDTO() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookRepository.findBooksByStatusAndAvailableItems(any(), any(), any()))
                .willReturn(BookTestData.getBooks());
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> actualResult = bookService.getAvailableBooks(SharedServiceTestData.SKOPJE_OFFICE_NAME);

        //  then
        assertThat(actualResult).isNotNull().containsExactlyElementsOf(bookDisplayDTOs);
    }

    @Test
    void getRequestedBooks_atLeastOneRequestedBookExists_returnsListOfBookDisplayDTO() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookRepository.findBookByBookStatusAndOfficeName(any(), any())).willReturn(BookTestData.getBooks());
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> actualResult = bookService.getRequestedBooks(SharedServiceTestData.SKOPJE_OFFICE_NAME);

        //  then
        assertThat(actualResult).isNotNull().containsExactlyElementsOf(bookDisplayDTOs);
    }

    @Test
    void getBookByISBN_bookWithGivenISBNExists_returnsBookDetailsDTO() {
        // given
        given(bookRepository.findByIsbnAndOfficeName(any(), any())).willReturn(Optional.of(BookTestData.getBook()));
        given(bookConverter.toBookDetailsDTO(any(), any())).willReturn(BookTestData.getBookDetailsDTO());
        given(reviewQueryService.getTopReviewsForDisplayInBookView(any(), any())).willReturn(
                ReviewTestData.getReviewResponseDTOs());

        // when
        BookDetailsDTO actualResult =
                bookService.getBookByIsbn(BookTestData.BOOK_ISBN, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(BookTestData.getBookDetailsDTO());

        verify(bookConverter).toBookDetailsDTO(BookTestData.getBook(), ReviewTestData.getReviewResponseDTOs());
    }

    @Test
    void getBookByIsbn_bookWithGivenISBNDoesNotExists_throwsException() {
        // given

        // when & then
        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(
                        () -> bookService.getBookByIsbn(BookTestData.BOOK_ISBN,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .withMessage("Book with ISBN: " + BookTestData.BOOK_ISBN + " not found");
    }

    @Test
    void getBooksByTitle_atLeastOneBookExistsWithGivenTitle_returnsListOfBookDisplayDTO() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookRepository.findByTitleContainingIgnoreCaseAndOfficeName(any(), any())).willReturn(
                BookTestData.getBooks());
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        //when
        List<BookDisplayDTO> actualResult =
                bookService.getBooksByTitle(BookTestData.BOOK_TITLE, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        //then
        assertThat(actualResult).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void getBooksByLanguage_atLeastOneBookWithGivenLanguageExists_returnsListOfBookDisplayDTO() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookRepository.findBooksByLanguageAndOfficeName(any(), any())).willReturn(BookTestData.getBooks());
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> actualResult =
                bookService.getBooksByLanguage(BookTestData.BOOK_LANGUAGE, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(bookDisplayDTOs);
    }

    @Test
    void getBooksByGenresContaining_atLeastOneBookForGivenGenresExists_returnsListBookDisplayDTOs() {
        // given
        List<BookDisplayDTO> bookDisplayDTOs = BookTestData.getBookDisplayDTOs();

        given(bookRepository.findBooksByGenresContaining(any(), any())).willReturn(BookTestData.getBooks());
        given(bookConverter.toBookDisplayDTO(any())).willReturn(bookDisplayDTOs.get(0), bookDisplayDTOs.get(1));

        // when
        List<BookDisplayDTO> actualResult =
                bookService.getBooksByGenresContaining(BookTestData.BOOK_GENRES,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(bookDisplayDTOs);
    }
}