package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OfficeServiceImplTest {

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private OfficeServiceImpl officeService;

    @Test
    void getAllOffices_atLeastOneOfficeExists_returnsListOfOfficeResponseDTO() {
        // given
        given(officeRepository.findAll()).willReturn(
                List.of(SharedServiceTestData.SKOPJE_OFFICE, SharedServiceTestData.SOFIJA_OFFICE));

        // when
        List<OfficeResponseDTO> actualResult = officeService.getAllOffices();

        //then
        assertThat(actualResult).containsExactly(SharedServiceTestData.SKOPJE_OFFICE_DTO,
                SharedServiceTestData.SOFIJA_OFFICE_DTO);
    }
}
