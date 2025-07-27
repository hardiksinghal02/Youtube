package com.youtube.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthException extends RuntimeException {

    private String errorMessage;
    private Integer errorCore;
    private AuthError authError;

    public AuthException(AuthError authError, String errorMessage ) {
        this(errorMessage, authError.getErrorCode(), authError);
        authError.setErrorMessage(errorMessage);
    }

    public AuthException(AuthError authError) {
        this(authError, authError.getErrorMessage());
    }
}
