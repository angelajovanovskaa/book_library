package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;

import java.util.List;

public interface OfficeService {
     List<OfficeResponseDTO> getAllOffices();
}
