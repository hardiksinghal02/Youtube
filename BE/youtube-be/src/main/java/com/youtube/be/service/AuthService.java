package com.youtube.be.service;

import com.youtube.be.dto.AuthResponseDto;

public interface AuthService {

    AuthResponseDto authenticateGoogleUser(String encodedJwt);

    Object authenticateUser(String accessToken);
}
