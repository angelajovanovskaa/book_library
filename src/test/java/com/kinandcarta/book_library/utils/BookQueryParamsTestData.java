package com.kinandcarta.book_library.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

@UtilityClass
public class BookQueryParamsTestData {
    private static final String LANGUAGE_PARAM = "language";
    private static final String GENRES_PARAM = "genres";

    public MultiValueMap<String, String> createQueryParamsForGetByBookWithCustomOfficeName(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByTitleWithCustomOfficeName(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE_SEARCH_TERM);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByTitleWithCustomTitleSearchTerm(String titleSearchTerm) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, titleSearchTerm);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByLanguageWithCustomOfficeName(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByLanguageWithCustomLanguage(String language) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(LANGUAGE_PARAM, language);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByGenreWithCustomOfficeName(String officeName) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        queryParamsValues.add(GENRES_PARAM, Arrays.toString(BookTestData.BOOK_GENRES));

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByGenreWithCustomGenre(String genre) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(GENRES_PARAM, genre);

        return queryParamsValues;
    }

    public static MultiValueMap<String, String> createQueryParamsWithNullOfficeName() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, null);
        return params;
    }

    public static MultiValueMap<String, String> createQueryParamsWithBlankOfficeName(String officeName) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        return params;
    }

    public static MultiValueMap<String, String> createQueryParamsWithEmptyOfficeName() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, "");
        return params;
    }

    public MultiValueMap<String, String> createQueryParamsForGetPaginatedBook() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.PAGE_SIZE_PARAM,
                String.valueOf(SharedServiceTestData.PAGE_SIZE));

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetBySearchTitle() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_TITLE_PARAM, BookTestData.BOOK_TITLE);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByLanguage() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(LANGUAGE_PARAM, BookTestData.BOOK_LANGUAGE);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsForGetByGenres() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(GENRES_PARAM, Arrays.toString(BookTestData.BOOK_GENRES));

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsInvalidIsbn() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_INVALID_ISBN);
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsWithOfficeAndISBN() {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        return queryParamsValues;
    }

    public MultiValueMap<String, String> createQueryParamsIsbn(String isbn) {
        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.BOOK_ISBN_PARAM, isbn);

        return queryParamsValues;
    }
}
