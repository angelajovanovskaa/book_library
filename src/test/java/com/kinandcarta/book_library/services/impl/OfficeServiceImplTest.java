package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.repositories.OfficeRepository;
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
    void getAllOffices_theListHasAtLeastOne_returnsListOfOfficeResponseDTO() {
        // given
        List<Office> offices = getOffices();
        List<OfficeResponseDTO> officeResponseDTOS = getOfficeResponseDTOs();

        given(officeRepository.findAll()).willReturn(offices);

        // when
        List<OfficeResponseDTO> result = officeService.getAllOffices();

        //then
        assertThat(result).isEqualTo(officeResponseDTOS);
    }

    private List<Office> getOffices() {
        Office office1 = new Office("Skopje");
        Office office2 = new Office("Sofija");

        return List.of(office1, office2);
    }

    private List<OfficeResponseDTO> getOfficeResponseDTOs() {
        OfficeResponseDTO office1 = new OfficeResponseDTO("Skopje");
        OfficeResponseDTO office2 = new OfficeResponseDTO("Sofija");

        return List.of(office1, office2);
    }
}
