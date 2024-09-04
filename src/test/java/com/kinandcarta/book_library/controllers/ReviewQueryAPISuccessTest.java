package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.ReviewResponseDTO;
import com.kinandcarta.book_library.services.ReviewManagementService;
import com.kinandcarta.book_library.services.ReviewQueryService;
import com.kinandcarta.book_library.utils.BookTestData;
import com.kinandcarta.book_library.utils.ReviewTestData;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewQueryAPISuccessTest {
    private static final String REVIEW_BASE_PATH = "/reviews";

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
        String jsonResult = performRequestAndExpectSuccess(REVIEW_BASE_PATH, params);

        List<ReviewResponseDTO> actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).containsExactly(ReviewTestData.getReviewResponseDTO());
    }

    @Test
    @SneakyThrows
    void getReviewById_reviewWithGivenIdExists_returnsReviewResponseDTO() {
        // given
        given(reviewQueryService.getReviewById(any())).willReturn(ReviewTestData.getReviewResponseDTO());

        // when
        String jsonResult = performRequestAndExpectSuccess(REVIEW_BASE_PATH + "/" + ReviewTestData.REVIEW_ID, null);

        ReviewResponseDTO actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).isEqualTo(ReviewTestData.getReviewResponseDTO());
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
        String jsonResult = performRequestAndExpectSuccess(REVIEW_BASE_PATH + "/top-reviews", params);

        List<ReviewResponseDTO> actualResult = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(actualResult).containsExactly(ReviewTestData.getReviewResponseDTO());
    }

    private String performRequestAndExpectSuccess(String path, MultiValueMap<String, String> params)
            throws Exception {
        return mockMvc.perform((params == null ? get(path) : get(path).queryParams(params)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }
}
