package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.kinandcarta.book_library.utils.OfficeTestData.OFFICE;
import static com.kinandcarta.book_library.utils.RequestedBookTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestedBookQueryServiceImplTest {
    @Mock
    private RequestedBookRepository requestedBookRepository;

    @Mock
    private RequestedBookConverter requestedBookConverter;

    @InjectMocks
    private RequestedBookQueryServiceImpl requestedBookQueryService;

    @Test
    void getAllRequestedBooksByOfficeName_atLeastOneRequestedBookExists_returnListOfRequestedBookDTOs() {
        // given
        List<RequestedBook> requestedBooks = getRequestedBooks();
        List<RequestedBookResponseDTO> requestedBookResponseDTOS = getRequestedBookResponseDTOs();
        final String officeName = OFFICE.getName();

        given(requestedBookRepository.findAllByBookOfficeName(any())).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                requestedBookResponseDTOS.get(0),
                requestedBookResponseDTOS.get(1)
        );

        // when
        List<RequestedBookResponseDTO> actualResult =
                requestedBookQueryService.getAllRequestedBooksByOfficeName(officeName);

        // then
        verify(requestedBookRepository).findAllByBookOfficeName(any());
        verify(requestedBookConverter, times(2)).toRequestedBookResponseDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTOS);
    }

    @Test
    void getRequestedBooksByBookStatus_givenStatus_returnListOfRequestedBookDTOs() {
        // given
        final BookStatus status = BookStatus.REQUESTED;
        final List<RequestedBook> requestedBooks = getRequestedBooks();
        final List<RequestedBookResponseDTO> requestedBookResponseDTOS = getRequestedBookResponseDTOs();
        final String officeName = OFFICE.getName();

        given(requestedBookRepository.findAllByBookBookStatusAndBookOfficeNameOrderByLikeCounterDescBookTitleAsc(any(),
                any())).willReturn(List.of(requestedBooks.get(0), requestedBooks.get(1)));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                requestedBookResponseDTOS.get(0),
                requestedBookResponseDTOS.get(1)
        );

        // when
        List<RequestedBookResponseDTO> actualResult =
                requestedBookQueryService.getRequestedBooksByBookStatusAndOfficeName(status, officeName);

        // then
        verify(requestedBookRepository).findAllByBookBookStatusAndBookOfficeNameOrderByLikeCounterDescBookTitleAsc(
                any(), any());
        verify(requestedBookConverter, times(2)).toRequestedBookResponseDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTOS);
    }

    @Test
    void getRequestedBookById_givenValidId_returnRequestedBookDTO() {
        // given
        UUID requestedBookId = UUID.randomUUID();
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookResponseDTO requestedBookResponseDTO = getRequestedBookDTO();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(requestedBookResponseDTO);

        // when
        RequestedBookResponseDTO actualResult = requestedBookQueryService.getRequestedBookById(requestedBookId);

        // then
        verify(requestedBookRepository).findById(any());
        verify(requestedBookConverter).toRequestedBookResponseDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTO);
    }

    @Test
    void getRequestedBookById_givenInvalidId_throwsException() {
        // given
        UUID requestedBookId = UUID.randomUUID();

        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookQueryService.getRequestedBookById(requestedBookId));

        verify(requestedBookRepository).findById(any());
        verify(requestedBookConverter, times(0)).toRequestedBookResponseDTO(any());
    }

    @Test
    void getRequestedBookByISBN_givenValidISBN_returnRequestedBookDTO() {
        // given
        String officeName = OFFICE.getName();
        RequestedBook requestedBook = getRequestedBook();
        Book book = requestedBook.getBook();
        String isbn = book.getIsbn();
        RequestedBookResponseDTO requestedBookResponseDTO = getRequestedBookDTO();

        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(
                Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(requestedBookResponseDTO);

        // when
        RequestedBookResponseDTO actualResult =
                requestedBookQueryService.getRequestedBookByISBNAndOfficeName(isbn, officeName);

        // then
        verify(requestedBookRepository).findByBookIsbnAndBookOfficeName(any(), any());
        verify(requestedBookConverter).toRequestedBookResponseDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTO);
    }

    @Test
    void getRequestedBookByISBN_givenInvalidISBN_throwsException() {
        // given
        final String isbn = "invalidISBN";
        final String officeName = OFFICE.getName();

        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookQueryService.getRequestedBookByISBNAndOfficeName(isbn, officeName));

        verify(requestedBookRepository).findByBookIsbnAndBookOfficeName(any(), any());
        verify(requestedBookConverter, times(0)).toRequestedBookResponseDTO(any());
    }
}