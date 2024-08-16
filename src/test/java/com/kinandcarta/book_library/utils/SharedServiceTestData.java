package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class SharedServiceTestData {
    public static final Office SKOPJE_OFFICE = new Office("Skopje");
    public static final OfficeResponseDTO SKOPJE_OFFICE_DTO = new OfficeResponseDTO("Skopje");
    public static final String SKOPJE_OFFICE_NAME = SKOPJE_OFFICE.getName();
    public static final Office SOFIJA_OFFICE = new Office("Sofija");
    public static final OfficeResponseDTO SOFIJA_OFFICE_DTO = new OfficeResponseDTO("Sofija");
    public static final LocalDate DATE_NOW = LocalDate.now();
    public static final LocalDate DATE_IN_2_DAYS = LocalDate.now().plusDays(2);
    public static final Integer PAGE_NUMBER = 0;
    public static final Integer PAGE_SIZE = 2;
}
