package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.converters.RequestedBookConverter;
import com.kinandcarta.book_library.dtos.RequestedBookResponseDTO;
import com.kinandcarta.book_library.exceptions.RequestedBookNotFoundException;
import com.kinandcarta.book_library.repositories.RequestedBookRepository;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.RequestedBookTestData;
import com.kinandcarta.book_library.utils.SharedTestData;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
        List<RequestedBookResponseDTO> requestedBookResponseDTOs = RequestedBookTestData.getRequestedBookResponseDTOs();

        given(requestedBookRepository.findAllByBookOfficeName(any())).willReturn(
                RequestedBookTestData.getRequestedBooks());
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                requestedBookResponseDTOs.get(0), requestedBookResponseDTOs.get(1));

        // when
        List<RequestedBookResponseDTO> actualResult =
                requestedBookQueryService.getAllRequestedBooksByOfficeName(SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(requestedBookResponseDTOs);
    }

    @Test
    void getRequestedBooksByBookStatus_givenStatus_returnListOfRequestedBookDTOs() {
        // given
        List<RequestedBookResponseDTO> requestedBookResponseDTOs =
                RequestedBookTestData.getRequestedBookResponseDTOs();

        given(requestedBookRepository.findAllByBookBookStatusAndBookOfficeNameOrderByLikeCounterDescBookTitleAsc(any(),
                any())).willReturn(RequestedBookTestData.getRequestedBooks());
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                requestedBookResponseDTOs.get(0), requestedBookResponseDTOs.get(1));

        // when
        List<RequestedBookResponseDTO> actualResult =
                requestedBookQueryService.getRequestedBooksByBookStatusAndOfficeName(BookTestData.BOOK_STATUS,
                        SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(requestedBookResponseDTOs);
    }

    @Test
    void getRequestedBookById_givenValidId_returnRequestedBookDTO() {
        // given
        given(requestedBookRepository.findById(any())).willReturn(
                Optional.of(RequestedBookTestData.getRequestedBook()));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                RequestedBookTestData.getRequestedBookResponseDTO());

        // when
        RequestedBookResponseDTO actualResult =
                requestedBookQueryService.getRequestedBookById(RequestedBookTestData.REQUESTED_BOOK_ID);

        // then
        assertThat(actualResult).isEqualTo(RequestedBookTestData.getRequestedBookResponseDTO());
    }

    @Test
    void getRequestedBookById_givenInvalidId_throwsException() {
        // given
        given(requestedBookRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(
                        () -> requestedBookQueryService.getRequestedBookById(RequestedBookTestData.REQUESTED_BOOK_ID));
    }

    @Test
    void getRequestedBookByISBN_givenValidISBN_returnRequestedBookDTO() {
        // given
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(
                Optional.of(RequestedBookTestData.getRequestedBook()));
        given(requestedBookConverter.toRequestedBookResponseDTO(any())).willReturn(
                RequestedBookTestData.getRequestedBookResponseDTO());

        // when
        RequestedBookResponseDTO actualResult =
                requestedBookQueryService.getRequestedBookByISBNAndOfficeName(BookTestData.BOOK_ISBN,
                        SharedTestData.SKOPJE_OFFICE_NAME);

        // then
        assertThat(actualResult).isEqualTo(RequestedBookTestData.getRequestedBookResponseDTO());
    }

    @Test
    void getRequestedBookByISBN_givenInvalidISBN_throwsException() {
        // given
        given(requestedBookRepository.findByBookIsbnAndBookOfficeName(any(), any())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThatExceptionOfType(RequestedBookNotFoundException.class)
                .isThrownBy(() -> requestedBookQueryService.getRequestedBookByISBNAndOfficeName(
                        BookTestData.BOOK_INVALID_ISBN,
                        SharedTestData.SKOPJE_OFFICE_NAME));
    }
}