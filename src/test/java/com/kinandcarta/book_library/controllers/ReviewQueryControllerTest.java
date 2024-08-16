package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewQueryController.class)
class ReviewQueryControllerTest {
    public static final String REVIEW_BASE_PATH = "/reviews";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void getAllReviewsForBook_atLeastOneReviewExists_returnsListOfReviewResponseDTO() {
        // given
        given(reviewQueryService.getAllReviewsByBookIsbnAndByOfficeName(any(), any())).willReturn(
                List.of(ReviewTestData.getReviewResponseDTO()));

        // when
        String jsonResult =
                mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(
                                SharedControllerTestData.getMapWithOfficeParamAndBookISBNParam()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<ReviewResponseDTO> actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).containsExactly(ReviewTestData.getReviewResponseDTO());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getAllReviewsForBook_invalidOfficeNameParam_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> params = SharedControllerTestData.getMapWithBookISBNParam();
        params.add(SharedControllerTestData.OFFICE_PARAM, officeName);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getAllReviewsForBook_invalidBookISBNParam_returnsBadRequest(String bookISBN) {
        // given
        MultiValueMap<String, String> params = SharedControllerTestData.getMapWithOfficeParam();
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, bookISBN);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewForIdExists_returnsReviewResponseDTO() {
        // given
        given(reviewQueryService.getReviewById(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        String jsonResult =
                mockMvc.perform(get(REVIEW_BASE_PATH + "/" + ReviewTestData.REVIEW_ID))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        ReviewResponseDTO actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.getReviewResponseDTO());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getReviewById_invalidReviewIdParam_returnsBadRequest(String reviewId) {
        // given

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/" + reviewId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewIdIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBookView_atLeastOneReviewExists_returnsListOfReviewResponseDTO() {
        // given
        given(reviewQueryService.getTopReviewsForDisplayInBookView(any(), any())).willReturn(
                List.of(ReviewTestData.getReviewResponseDTO()));

        // when
        String jsonResult =
                mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(
                                SharedControllerTestData.getMapWithOfficeParamAndBookISBNParam()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<ReviewResponseDTO> actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).containsExactly(ReviewTestData.getReviewResponseDTO());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getTopReviewsForBookView_invalidOfficeNameParam_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> params = SharedControllerTestData.getMapWithBookISBNParam();
        params.add(SharedControllerTestData.OFFICE_PARAM, officeName);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(params))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getTopReviewsForBookView_invalidBookISBNParam_returnsBadRequest(String bookISBN) {
        // given
        MultiValueMap<String, String> params = SharedControllerTestData.getMapWithOfficeParam();
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, bookISBN);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(params))
                .andExpect(status().isBadRequest());
    }
}
