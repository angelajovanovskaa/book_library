package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import com.kinandcarta.book_library.repositories.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl {
    private final OfficeRepository officeRepository;

    public List<OfficeResponseDTO> getAllOffices() {
        List<Office> offices = officeRepository.findAll();
        return offices.stream().map(x -> new OfficeResponseDTO(x.getName())).toList();
    }
}
