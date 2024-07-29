package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookDTO;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.services.RequestedBookQueryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    void getAllRequestedBooks_atLeastOneRequestedBookExists_returnListOfRequestedBookDTOs() {
        // given
        List<RequestedBook> requestedBooks = getRequestedBooks();
        List<RequestedBookDTO> requestedBookDTOS = getRequestedBookDTOs();

        given(requestedBookRepository.findAll()).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(
                requestedBookDTOS.get(0),
                requestedBookDTOS.get(1),
                requestedBookDTOS.get(2)
        );

        // when
        List<RequestedBookDTO> actualResult = requestedBookQueryService.getAllRequestedBooks();

        // then
        verify(requestedBookRepository).findAll();
        verify(requestedBookConverter, times(3)).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

    @Test
    void getRequestedBooksByBookStatus_givenStatus_returnListOfRequestedBookDTOs() {
        // given
        BookStatus status = BookStatus.REQUESTED;
        List<RequestedBook> requestedBooks = getRequestedBooks();
        List<RequestedBookDTO> requestedBookDTOS = getRequestedBookDTOs();

        given(requestedBookRepository.findAllByBookBookStatusOrderByLikeCounterDescBookTitleAsc(status))
                .willReturn(List.of(requestedBooks.get(0), requestedBooks.get(1), requestedBooks.get(2)));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(
                requestedBookDTOS.get(0),
                requestedBookDTOS.get(1),
                requestedBookDTOS.get(2)
        );

        // when
        List<RequestedBookDTO> actualResult = requestedBookQueryService.getRequestedBooksByBookStatus(status);

        // then
        verify(requestedBookRepository).findAllByBookBookStatusOrderByLikeCounterDescBookTitleAsc(status);
        verify(requestedBookConverter, times(3)).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookDTOS);
    }

    @Test
    void getRequestedBookById_givenValidId_returnRequestedBookDTO() {
        // given
        UUID requestedBookId = UUID.randomUUID();
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findById(requestedBookId)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        // when
        RequestedBookDTO actualResult = requestedBookQueryService.getRequestedBookById(requestedBookId);

        // then
        verify(requestedBookRepository).findById(requestedBookId);
        verify(requestedBookConverter).toRequestedBookDTO(requestedBook);

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    void getRequestedBookById_givenInvalidId_throwsException() {
        // given
        UUID requestedBookId = UUID.randomUUID();

        given(requestedBookRepository.findById(requestedBookId)).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookQueryService.getRequestedBookById(requestedBookId));

        verify(requestedBookRepository).findById(requestedBookId);
        verify(requestedBookConverter, times(0)).toRequestedBookDTO(any());
    }

    @Test
    void getRequestedBookByISBN_givenValidISBN_returnRequestedBookDTO() {
        // given
        String isbn = "isbn1";
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookDTO requestedBookDTO = getRequestedBookDTO();

        given(requestedBookRepository.findByBookIsbn(isbn)).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookDTO);

        // when
        RequestedBookDTO actualResult = requestedBookQueryService.getRequestedBookByISBN(isbn);

        // then
        verify(requestedBookRepository).findByBookIsbn(isbn);
        verify(requestedBookConverter).toRequestedBookDTO(requestedBook);

        assertThat(actualResult).isEqualTo(requestedBookDTO);
    }

    @Test
    void getRequestedBookByISBN_givenInvalidISBN_throwsException() {
        // given
        String isbn = "invalidISBN";

        given(requestedBookRepository.findByBookIsbn(isbn)).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookQueryService.getRequestedBookByISBN(isbn));

        verify(requestedBookRepository).findByBookIsbn(isbn);
        verify(requestedBookConverter, times(0)).toRequestedBookDTO(any());
    }

    private List<RequestedBook> getRequestedBooks() {
        RequestedBook requestedBook1 = new RequestedBook(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                1L,
                null,
                new HashSet<>()
        );
        RequestedBook requestedBook2 = new RequestedBook(
                UUID.fromString("123e4567-e89b-12d3-a456-200000000000"),
                LocalDate.now(),
                2L,
                null,
                new HashSet<>()
        );
        RequestedBook requestedBook3 = new RequestedBook(
                UUID.fromString("123e4567-e89b-12d3-a456-300000000000"),
                LocalDate.now(),
                3L,
                null,
                new HashSet<>()
        );

        return List.of(requestedBook1, requestedBook2, requestedBook3);
    }


    private List<RequestedBookDTO> getRequestedBookDTOs() {
        RequestedBookDTO requestedBookDTO1 = new RequestedBookDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                1L,
                "isbn1",
                BookStatus.REQUESTED,
                "title1",
                "image1"
        );
        RequestedBookDTO requestedBookDTO2 = new RequestedBookDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-2" +
                        "00000000000"),
                LocalDate.now(),
                3L,
                "isbn2",
                BookStatus.REQUESTED,
                "title2",
                "image2"
        );
        RequestedBookDTO requestedBookDTO3 = new RequestedBookDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-300000000000"),
                LocalDate.now(),
                1L,
                "isbn3",
                BookStatus.REQUESTED,
                "title3",
                "image3"
        );

        return List.of(requestedBookDTO1, requestedBookDTO2, requestedBookDTO3);
    }

    private RequestedBook getRequestedBook() {
        return getRequestedBooks().getFirst();
    }

    private RequestedBookDTO getRequestedBookDTO() {
        return getRequestedBookDTOs().getFirst();
    }
}