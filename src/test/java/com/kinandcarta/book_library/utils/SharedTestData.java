package com.kinandcarta.book_library.utils;

import com.kinandcarta.book_library.entities.Office;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SharedTestData {
    public static final Office SKOPJE_OFFICE = new Office("Skopje");
    public static final Office SOFIJA_OFFICE = new Office("Sofija");
    public static final LocalDate DATE_NOW = LocalDate.now();
    public static final Integer PAGE_NUMBER = 0;
    public static final Integer PAGE_SIZE = 2;
}
