package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.dtos.UserLoginRequestDTO;

import java.io.IOException;

public interface AuthenticationService {
    String generateToken(UserLoginRequestDTO userLoginRequestDTO) throws IOException;
}
