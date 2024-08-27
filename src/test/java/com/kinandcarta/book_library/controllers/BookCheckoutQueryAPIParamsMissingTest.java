package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.services.impl.BookCheckoutManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.BookCheckoutQueryServiceImpl;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookCheckoutController.class)
class BookCheckoutQueryAPIParamsMissingTest {
    private static final String BOOK_CHECKOUTS_PATH = "/book-checkouts";

    @MockBean
    private BookCheckoutQueryServiceImpl bookCheckoutQueryService;

    @MockBean
    private BookCheckoutManagementServiceImpl bookCheckoutManagementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void getBookCheckouts_ParamOfficeNameMissing_returnsBadRequest() {
        // given & when & then
        mockMvc.perform(get(BOOK_CHECKOUTS_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsPaginated_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getPaginatedBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/paginated";

        // when && then
        mockMvc.perform(get(getPaginatedBookCheckoutsPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getActiveBookCheckouts_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getActiveBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/active";

        // when && then
        mockMvc.perform(get(getActiveBookCheckoutsPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getPastBookCheckouts_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getPastBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/past";

        // when && then
        mockMvc.perform(get(getPastBookCheckoutsPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsNearReturnDate_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getNearReturnDateBookCheckoutsPath = BOOK_CHECKOUTS_PATH + "/near-return-date";

        // when && then
        mockMvc.perform(get(getNearReturnDateBookCheckoutsPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamOfficeNameMissing_returnsBadRequest() {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when && then
        mockMvc.perform(get(getBookCheckoutsByTitleContainingPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'officeName' is not present."));
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByTitleContaining_ParamTitleSearchTermMissing_returnsBadRequest() {
        // given
        final String getBookCheckoutsByTitleContainingPath = BOOK_CHECKOUTS_PATH + "/by-title";

        // when && then
        mockMvc.perform(get(getBookCheckoutsByTitleContainingPath).queryParam(SharedControllerTestData.OFFICE_PARAM,
                        SharedServiceTestData.SKOPJE_OFFICE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'titleSearchTerm' is not present."));
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUser_ParamUserIdMissing_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutPath = BOOK_CHECKOUTS_PATH + "/by-user";

        // when && then
        mockMvc.perform(get(getUsersBookCheckoutPath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'userId' is not present."));
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUserAndTitleContaining_ParamUserIdMissing_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/by-user-and-title";

        // when && then
        mockMvc.perform(get(getUsersBookCheckoutByTitlePath))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'userId' is not present."));
    }

    @Test
    @SneakyThrows
    void getBookCheckoutsByUserAndTitleContaining_ParamTitleSearchTermMissing_returnsBadRequest() {
        // given
        final String getUsersBookCheckoutByTitlePath =
                BOOK_CHECKOUTS_PATH + "/by-user-and-title";

        // when && then
        mockMvc.perform(get(getUsersBookCheckoutByTitlePath).queryParam(SharedControllerTestData.USER_ID_PARAM,
                        UserTestData.USER_ID.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'titleSearchTerm' is not present."));
    }
}
