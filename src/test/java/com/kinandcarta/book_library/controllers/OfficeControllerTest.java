package com.kinandcarta.book_library.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.services.impl.OfficeServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OfficeController.class)
class OfficeControllerTest {
    private static final String OFFICES_PATH = "/offices";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfficeServiceImpl officeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void getOffices_atLeastOneOffice_Exists_returnListOfOffices() {
        // given
        final List<OfficeResponseDTO> officeResponseDTOS = officeService.getOffices();
        given(officeService.getOffices()).willReturn(officeResponseDTOS);

        // when
        final String jsonResult = mockMvc.perform(get(OFFICES_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<OfficeResponseDTO> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });

        // then
        assertThat(result).isEqualTo(officeResponseDTOS);
    }
}