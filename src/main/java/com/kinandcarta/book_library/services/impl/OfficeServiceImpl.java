package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import com.kinandcarta.book_library.services.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link OfficeService}.<br>
 * This service provides operations related to accessing office information
 */
@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {
    private final OfficeRepository officeRepository;

    /**
     * Retrieves a list of all offices available in the system.
     *
     * @return A list of {@link OfficeResponseDTO} objects, each containing the office name.
     */
    public List<OfficeResponseDTO> getAllOffices() {
        List<Office> offices = officeRepository.findAll();
        return offices.stream().map(office -> new OfficeResponseDTO(office.getName())).toList();
    }
}
