package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.entities.RequestedBook;
import com.kinandcarta.book_library.enums.BookStatus;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
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
    private final Office OFFICE = new Office("Skopje");

    @Mock
    private RequestedBookRepository requestedBookRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private RequestedBookConverter requestedBookConverter;

    @InjectMocks
    private RequestedBookQueryServiceImpl requestedBookQueryService;

    @Test
    void getAllRequestedBooksByOfficeName_atLeastOneRequestedBookExists_returnListOfRequestedBookDTOs() {
        // given
        List<RequestedBook> requestedBooks = getRequestedBooks();
        List<RequestedBookResponseDTO> requestedBookResponseDTOS = getRequestedBookDTOs();
        final String officeName = OFFICE.getName();

        given(officeRepository.findById(any())).willReturn(Optional.of(OFFICE));
        given(requestedBookRepository.findAllByBookOfficeName(any())).willReturn(requestedBooks);
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(
                requestedBookResponseDTOS.get(0),
                requestedBookResponseDTOS.get(1),
                requestedBookResponseDTOS.get(2)
        );

        // when
        List<RequestedBookResponseDTO> actualResult =
                requestedBookQueryService.getAllRequestedBooksByOfficeName(officeName);

        // then
        verify(officeRepository).findById(any());
        verify(requestedBookRepository).findAllByBookOfficeName(any());
        verify(requestedBookConverter, times(3)).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTOS);
    }

    @Test
    void getRequestedBooksByBookStatus_givenStatus_returnListOfRequestedBookDTOs() {
        // given
        final BookStatus status = BookStatus.REQUESTED;
        final List<RequestedBook> requestedBooks = getRequestedBooks();
        final List<RequestedBookResponseDTO> requestedBookResponseDTOS = getRequestedBookDTOs();
        final String officeName = OFFICE.getName();

        given(requestedBookRepository.findAllByBookBookStatusAndBookOfficeNameOrderByLikeCounterDescBookTitleAsc(any(),
                any())).willReturn(List.of(requestedBooks.get(0), requestedBooks.get(1), requestedBooks.get(2)));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(
                requestedBookResponseDTOS.get(0),
                requestedBookResponseDTOS.get(1),
                requestedBookResponseDTOS.get(2)
        );

        // when
        List<RequestedBookResponseDTO> actualResult =
                requestedBookQueryService.getRequestedBooksByBookStatusAndOfficeName(status, officeName);

        // then
        verify(requestedBookRepository).findAllByBookBookStatusAndBookOfficeNameOrderByLikeCounterDescBookTitleAsc(
                any(), any());
        verify(requestedBookConverter, times(3)).toRequestedBookDTO(any());

        assertThat(actualResult).isEqualTo(requestedBookResponseDTOS);
    }

    @Test
    void getRequestedBookById_givenValidId_returnRequestedBookDTO() {
        // given
        UUID requestedBookId = UUID.randomUUID();
        RequestedBook requestedBook = getRequestedBook();
        RequestedBookResponseDTO requestedBookResponseDTO = getRequestedBookDTO();

        given(requestedBookRepository.findById(any())).willReturn(Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookResponseDTO);

        // when
        RequestedBookResponseDTO actualResult = requestedBookQueryService.getRequestedBookById(requestedBookId);

        // then
        verify(requestedBookRepository).findById(any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

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
        verify(requestedBookConverter, times(0)).toRequestedBookDTO(any());
    }

    @Test
    void getRequestedBookByISBN_givenValidISBN_returnRequestedBookDTO() {
        // given
        final String isbn = "isbn1";
        final String officeName = OFFICE.getName();
        final RequestedBook requestedBook = getRequestedBook();
        final RequestedBookResponseDTO requestedBookResponseDTO = getRequestedBookDTO();

        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(
                Optional.of(requestedBook));
        given(requestedBookConverter.toRequestedBookDTO(any())).willReturn(requestedBookResponseDTO);

        // when
        RequestedBookResponseDTO actualResult =
                requestedBookQueryService.getRequestedBookByISBNAndOfficeName(isbn, officeName);

        // then
        verify(requestedBookRepository).findByBookIsbnAndBookOfficeName(any(), any());
        verify(requestedBookConverter).toRequestedBookDTO(any());

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


    private List<RequestedBookResponseDTO> getRequestedBookDTOs() {
        RequestedBookResponseDTO requestedBookResponseDTO1 = new RequestedBookResponseDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-100000000000"),
                LocalDate.now(),
                1L,
                "isbn1",
                BookStatus.REQUESTED,
                "title1",
                "image1"
        );
        RequestedBookResponseDTO requestedBookResponseDTO2 = new RequestedBookResponseDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-2" +
                        "00000000000"),
                LocalDate.now(),
                3L,
                "isbn2",
                BookStatus.REQUESTED,
                "title2",
                "image2"
        );
        RequestedBookResponseDTO requestedBookResponseDTO3 = new RequestedBookResponseDTO(
                UUID.fromString("123e4567-e89b-12d3-a456-300000000000"),
                LocalDate.now(),
                1L,
                "isbn3",
                BookStatus.REQUESTED,
                "title3",
                "image3"
        );

        return List.of(requestedBookResponseDTO1, requestedBookResponseDTO2, requestedBookResponseDTO3);
    }

    private RequestedBook getRequestedBook() {
        return getRequestedBooks().getFirst();
    }

    private RequestedBookResponseDTO getRequestedBookDTO() {
        return getRequestedBookDTOs().getFirst();
    }
}