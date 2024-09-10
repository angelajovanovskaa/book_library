package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.utils.UserTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    private static final String MOCK_TOKEN = "mockedToken";

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void generateToken_theRequestIsValid_returnsJwtToken() throws IOException {
        // given
        UserLoginRequestDTO userLoginRequestDTO = UserTestData.getUserLoginRequestDTO();
        String userEmail = userLoginRequestDTO.userEmail();
        String userPassword = userLoginRequestDTO.userPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, userPassword);

        given(jwtService.generateToken(userEmail)).willReturn(MOCK_TOKEN);

        // when
        String result = authenticationService.generateToken(userLoginRequestDTO);

        // then
        assertThat(result).isEqualTo(MOCK_TOKEN);
        verify(authenticationManager).authenticate(authenticationToken);
    }

    @Test
    void generateToken_theRequestIsInvalid_throwsIOException() throws IOException {
        // given
        UserLoginRequestDTO userLoginRequestDTO = UserTestData.getUserLoginRequestDTO();
        String userEmail = userLoginRequestDTO.userEmail();

        given(jwtService.generateToken(userEmail)).willThrow(new IOException());

        // when && then
        assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> authenticationService.generateToken(userLoginRequestDTO))
                .withMessage(null);
    }
}