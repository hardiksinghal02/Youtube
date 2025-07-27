package com.youtube.be.controllers;

import com.youtube.be.dto.ResponseDto;
import com.youtube.be.dto.AuthResponseDto;
import com.youtube.be.exception.AuthException;
import com.youtube.be.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/google")
    public ResponseDto<AuthResponseDto> googleAuth(@RequestBody String encodedJwt) {
        try {
            AuthResponseDto authResponse = authService.authenticateGoogleUser(encodedJwt);
            return ResponseDto.success(authResponse);
        } catch (AuthException e) {
            return ResponseDto.failure(e.getAuthError());
        }
    }

    @PostMapping(value = "/verify")
    public ResponseDto<Object> verify(@RequestBody String accessToken) {
        try {
            Object authResponse = authService.authenticateUser(accessToken);
            return ResponseDto.success(authResponse);
        } catch (AuthException e) {
            return ResponseDto.failure(e.getAuthError());
        } catch (Exception e) {
            return ResponseDto.failure(e.getMessage());
        }
    }
}
