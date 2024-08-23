package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.UserProfileDTO;
import com.kinandcarta.book_library.dtos.UserWithRoleDTO;
import com.kinandcarta.book_library.services.impl.UserManagementServiceImpl;
import com.kinandcarta.book_library.services.impl.UserQueryServiceImpl;
import com.kinandcarta.book_library.utils.SharedControllerTestData;
import com.kinandcarta.book_library.utils.SharedServiceTestData;
import com.kinandcarta.book_library.utils.UserTestData;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserQueryAPISuccessTest {
    private static final String USERS_PATH = "/users";

    @MockBean
    private UserQueryServiceImpl userService;

    @MockBean
    private UserManagementServiceImpl userManagementService;

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
        final String jsonResult =
                mockMvc.perform(get(USERS_PATH).queryParam(SharedControllerTestData.OFFICE_PARAM,
                                SharedServiceTestData.SKOPJE_OFFICE_NAME))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

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

        MultiValueMap<String, String> queryParamsValues = new LinkedMultiValueMap<>();
        queryParamsValues.add(SharedControllerTestData.OFFICE_PARAM, SharedServiceTestData.SKOPJE_OFFICE_NAME);
        queryParamsValues.add(SharedControllerTestData.FULL_NAME_PARAM, UserTestData.USER_FULL_NAME);

        // when
        final String jsonResult =
                mockMvc.perform(get(getUsersByFullNamePath).queryParams(queryParamsValues))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

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
        final String jsonResult =
                mockMvc.perform(get(getUserProfilePath).queryParam(SharedControllerTestData.USER_ID_PARAM,
                                UserTestData.USER_ID.toString()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        UserProfileDTO result = objectMapper.readValue(jsonResult, UserProfileDTO.class);

        // then
        assertThat(result).isEqualTo(userProfileDTO);
    }
}
