package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.entities.Office;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OfficeTestData {
    public static final Office OFFICE = new Office("Skopje");
    public static final Office OTHER_OFFICE = new Office("Sofija");

    public static List<Office> getOffices() {
        Office office1 = new Office("Skopje");
        Office office2 = new Office("Sofija");

        return List.of(office1, office2);
    }

    public static List<OfficeResponseDTO> getOfficeResponseDTOs() {
        OfficeResponseDTO office1 = new OfficeResponseDTO("Skopje");
        OfficeResponseDTO office2 = new OfficeResponseDTO("Sofija");

        return List.of(office1, office2);
    }
}
