package com.kinandcarta.book_library.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@UtilityClass
public class SharedControllerTestData {
    public static final String OFFICE_PARAM = "officeName";
    public static final String BOOK_TITLE_PARAM = "titleSearchTerm";
    public static final String BOOK_ISBN_PARAM = "bookISBN";
    public static final String USER_ID_PARAM = "userId";
    public static final String PAGE_SIZE_PARAM = "pageSize";
    public static final String CONTENT_KEY_VALUE = "content";

    public static MultiValueMap<String, String> getMapWithOfficeParamAndBookISBNParam(){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        map.add(BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        return map;
    }

    public static MultiValueMap<String, String> getMapWithOfficeParam(){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        return map;
    }

    public static MultiValueMap<String, String> getMapWithBookISBNParam(){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        return map;
    }
}
