package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.services.impl.AuthenticationServiceImpl;
import com.kinandcarta.book_library.services.impl.UserManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserQueryAPISuccessTest {
    private static final String USERS_PATH = "/users";

    @MockBean
    private UserQueryServiceImpl userService;

    @MockBean
    private UserManagementServiceImpl userManagementService;

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUsers_atLeastOneUserExists_returnsListOfUserWithRoleDTO() throws Exception {
        // given
        List<UserWithRoleDTO> userWithRoleDTOs = UserTestData.getUserWithRoleResponseDTOs();

        given(userService.getUsers(SharedServiceTestData.SKOPJE_OFFICE_NAME)).willReturn(userWithRoleDTOs);

        // when
        final String jsonResult = performRequestAndExpectJsonResult(USERS_PATH, SharedControllerTestData.OFFICE_PARAM
                , SharedServiceTestData.SKOPJE_OFFICE_NAME);

        List<UserWithRoleDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(userWithRoleDTOs);
    }

    @Test
    void getUsersByFullName_atLeastOneUserExists_returnsListOfUserWithRoleDTO() throws Exception {
        // given
        final String getUsersByFullNamePath = USERS_PATH + "/by-full-name";
        UserWithRoleDTO userWithRoleDTO = UserTestData.getUserWithRoleResponseDTOs().getFirst();

        given(userService.getUsersWithFullName(SharedServiceTestData.SKOPJE_OFFICE_NAME,
                UserTestData.USER_FULL_NAME)).willReturn(List.of(userWithRoleDTO));

        // when
        final String jsonResult = performRequestAndExpectJsonResult(getUsersByFullNamePath,
                UserTestData.getUsersByFullNameDefaultQueryParams());

        List<UserWithRoleDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).containsExactly(userWithRoleDTO);
    }

    @Test
    void getUserProfile_userExists_returnsUserProfileDTO() throws Exception {
        // given
        final String getUserProfilePath = USERS_PATH + "/profile";
        UserProfileDTO userProfileDTO = UserTestData.getUserProfileDTO();

        given(userService.getUserProfile(UserTestData.USER_ID)).willReturn(userProfileDTO);

        // when
        final String jsonResult = performRequestAndExpectJsonResult(getUserProfilePath,
                SharedControllerTestData.USER_ID_PARAM, UserTestData.USER_ID.toString());

        UserProfileDTO result = objectMapper.readValue(jsonResult, UserProfileDTO.class);

        // then
        assertThat(result).isEqualTo(userProfileDTO);
    }

    private String performRequestAndExpectJsonResult(String path, String param, String paramValue) throws Exception {
        return mockMvc.perform(get(path).queryParam(param, paramValue))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    private String performRequestAndExpectJsonResult(String path, MultiValueMap<String, String> paramValues)
            throws Exception {
        return mockMvc.perform(get(path).queryParams(paramValues))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }
}
