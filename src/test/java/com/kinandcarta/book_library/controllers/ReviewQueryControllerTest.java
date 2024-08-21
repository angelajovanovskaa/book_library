package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.exceptions.ReviewNotFoundException;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewQueryControllerTest {
    public static final String REVIEW_BASE_PATH = "/reviews";

    @MockBean
    private ReviewQueryService reviewQueryService;

    @MockBean
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void getReviewsForBook_atLeastOneReviewExists_returnsListOfReviewResponseDTO() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        given(reviewQueryService.getAllReviewsByBookIsbnAndByOfficeName(any(), any())).willReturn(
                List.of(ReviewTestData.getReviewResponseDTO()));

        // when
        String jsonResult =
                mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<ReviewResponseDTO> actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).containsExactly(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(
                        get(REVIEW_BASE_PATH).queryParam(SharedControllerTestData.BOOK_ISBN_PARAM,
                                BookTestData.BOOK_ISBN))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsEmpty_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failure"));
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_officeNameParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, null);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_isbnParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'isbn' is not present."));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getReviewsForBook_isbnParamIsEmpty_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, isbn);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failure"));
    }

    @Test
    @SneakyThrows
    void getReviewsForBook_isbnParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, null);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH).queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'isbn' is not present."));
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewWithGivenIdExists_returnsReviewResponseDTO() {
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
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getReviewById_reviewIdParamIsNotValid_returnsBadRequest(String reviewId) {
        // given

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/" + reviewId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required path variable 'reviewId' is not present."));
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewIdParamIsEmpty_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No static resource reviews."));
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewIdParamIsNull_returnsBadRequest() {
        // given

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/" + null))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.detail").value("Failed to convert 'reviewId' with value: 'null'"));
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewDoesNotExist_returnsNotFound() {
        // given
        given(reviewQueryService.getReviewById(ReviewTestData.REVIEW_ID)).willThrow(
                new ReviewNotFoundException(ReviewTestData.REVIEW_ID));

        //when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/" + ReviewTestData.REVIEW_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_atLeastOneReviewExists_returnsListOfReviewResponseDTO() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        given(reviewQueryService.getTopReviewsForBook(any(), any())).willReturn(
                List.of(ReviewTestData.getReviewResponseDTO()));

        // when
        String jsonResult =
                mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(params))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        List<ReviewResponseDTO> actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).containsExactly(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsEmpty_returnsBadRequest(String officeName) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, officeName);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failure"));
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_officeNameParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, null);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, BookTestData.BOOK_ISBN);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsMissing_returnsBadRequest() {
        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews")
                        .queryParam(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'isbn' is not present."));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsEmpty_returnsBadRequest(String isbn) {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, isbn);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failure"));
    }

    @Test
    @SneakyThrows
    void getTopReviewsForBook_isbnParamIsNull_returnsBadRequest() {
        // given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        params.add(SharedControllerTestData.BOOK_ISBN_PARAM, null);

        // when & then
        mockMvc.perform(get(REVIEW_BASE_PATH + "/top-reviews").queryParams(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'isbn' is not present."));
    }
}