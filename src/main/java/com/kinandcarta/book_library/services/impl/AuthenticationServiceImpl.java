package com.kinandcarta.book_library.services.impl;

import com.kinandcarta.book_library.jwt.JwtService;
import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;
import com.kinandcarta.book_library.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Implementation of the {@link AuthenticationService} interface that provides
 * authentication and token generation service.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates the user based on the provided credentials and generates a JWT token
     * if the authentication is successful.
     *
     * @param userLoginRequestDTO the DTO containing information about users profile.
     * @return a JWT token if the user is successfully authenticated.
     * @throws IOException if there is an issue with token generation.
     */
    @Override
    public String generateToken(UserLoginRequestDTO userLoginRequestDTO) throws IOException {
        String userEmail = userLoginRequestDTO.userEmail();
        String userPassword = userLoginRequestDTO.userPassword();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, userPassword);
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        return jwtService.generateToken(userLoginRequestDTO.userEmail());
    }
}
